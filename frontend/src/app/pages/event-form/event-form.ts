import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';

import { EventResponse } from '../../models/event.model';
import { ProblemDetail } from '../../models/problem-detail.model';
import { EventService } from '../../services/event.service';

// ---------- Validateurs personnalisés (mêmes règles que le backend) ----------

// @Future : la date doit être dans le futur
const futureDate: ValidatorFn = (control: AbstractControl): ValidationErrors | null =>
  control.value && new Date(control.value) <= new Date() ? { future: true } : null;

// Règle sur DEUX champs : la billetterie ouvre avant l'événement (InvalidEventDatesException)
const salesBeforeStart: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const startsAt = group.get('startsAt')?.value;
  const salesStartAt = group.get('salesStartAt')?.value;
  return startsAt && salesStartAt && new Date(salesStartAt) >= new Date(startsAt)
    ? { salesAfterStart: true }
    : null;
};

// ---------- Conversions de dates ----------

// "2026-12-20T21:00" (champ datetime-local, heure locale) -> "2026-12-20T20:00:00.000Z" (Instant Java, UTC)
const toIso = (localDateTime: string): string => new Date(localDateTime).toISOString();

// Inverse : "2026-12-20T20:00:00Z" (API, UTC) -> "2026-12-20T21:00" (champ datetime-local)
const toLocalInput = (iso: string): string => {
  const d = new Date(iso);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

// Un seul formulaire, deux modes :
//   /events/new      -> création     (US1) : POST, avec les catégories
//   /events/:id/edit -> modification (US4) : PUT, sans les catégories (elles ont leurs propres endpoints)
@Component({
  selector: 'app-event-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './event-form.html',
})
export class EventForm implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly eventService = inject(EventService);
  private readonly router = inject(Router);

  // Présent seulement sur /events/:id/edit (withComponentInputBinding)
  readonly id = input<string>();
  protected readonly isEdit = computed(() => this.id() !== undefined);

  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly serverError = signal<string | null>(null);

  // Le formulaire et ses règles, décrits en TypeScript
  protected readonly form = this.fb.group(
    {
      title: ['', [Validators.required, Validators.maxLength(150)]],
      description: ['', Validators.maxLength(2000)],
      venue: ['', Validators.required],
      startsAt: ['', [Validators.required, futureDate]],
      salesStartAt: ['', Validators.required],
      categories: this.fb.array([this.newCategory()]), // au moins une catégorie (RG11)
    },
    { validators: salesBeforeStart },
  );

  protected get categories() {
    return this.form.controls.categories;
  }

  ngOnInit(): void {
    if (this.isEdit()) {
      // Désactivé = ignoré par la validation : les catégories ne font pas partie d'une modification
      this.categories.disable();
      this.loadEvent(Number(this.id()));
    }
  }

  // Mode modification : pré-remplit le formulaire avec l'événement existant
  private loadEvent(id: number): void {
    this.loading.set(true);
    this.eventService.getById(id).subscribe({
      next: (event) => {
        this.form.patchValue({
          title: event.title,
          description: event.description ?? '',
          venue: event.venue,
          startsAt: toLocalInput(event.startsAt),
          salesStartAt: toLocalInput(event.salesStartAt),
        });
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.showServerErrors(err);
      },
    });
  }

  // ---------- FormArray : liste dynamique de catégories ----------

  protected addCategory(): void {
    this.categories.push(this.newCategory());
  }

  protected removeCategory(index: number): void {
    if (this.categories.length > 1) {
      this.categories.removeAt(index);
    }
  }

  private newCategory() {
    return this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(50)]],
      price: [0, [Validators.required, Validators.min(0)]],
      capacity: [100, [Validators.required, Validators.min(1)]],
    });
  }

  // ---------- Envoi ----------

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched(); // affiche toutes les erreurs d'un coup
      return;
    }
    this.submitting.set(true);
    this.serverError.set(null);

    const value = this.form.getRawValue();
    const common = {
      title: value.title.trim(),
      description: value.description.trim() || null,
      venue: value.venue.trim(),
      startsAt: toIso(value.startsAt),
      salesStartAt: toIso(value.salesStartAt),
    };

    const request$: Observable<EventResponse> = this.isEdit()
      ? this.eventService.update(Number(this.id()), common) // PUT : sans catégories
      : this.eventService.create({
          ...common,
          categories: value.categories.map((c) => ({
            name: c.name.trim(),
            price: c.price,
            capacity: c.capacity,
          })),
        });

    request$.subscribe({
      next: (event) => this.router.navigate(['/events', event.id]), // -> page détail
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        this.showServerErrors(err);
      },
    });
  }

  // Affiche le ProblemDetail du backend : message général + erreurs sous les bons champs
  private showServerErrors(err: HttpErrorResponse): void {
    if (err.status === 0) {
      this.serverError.set('Impossible de contacter le serveur. Le backend est-il démarré ?');
      return;
    }
    const problem = err.error as ProblemDetail | null;
    this.serverError.set(problem?.detail ?? 'Une erreur est survenue.');

    // "categories[0].price" (Spring) -> "categories.0.price" (chemin Angular)
    for (const [field, message] of Object.entries(problem?.errors ?? {})) {
      const path = field.replace(/\[(\d+)]/g, '.$1');
      this.form.get(path)?.setErrors({ server: message });
    }
  }

  // ---------- Message d'erreur d'un champ ----------

  protected errorOf(control: AbstractControl): string | null {
    if (!control.errors || !control.touched) {
      return null; // pas d'erreur affichée tant que l'utilisateur n'a pas touché le champ
    }
    const errors = control.errors;
    if (errors['required']) return 'Champ obligatoire';
    if (errors['maxlength']) return `${errors['maxlength'].requiredLength} caractères maximum`;
    if (errors['min']) return `Doit être supérieur ou égal à ${errors['min'].min}`;
    if (errors['future']) return 'La date doit être dans le futur';
    if (errors['server']) return errors['server'];
    return 'Valeur invalide';
  }
}
