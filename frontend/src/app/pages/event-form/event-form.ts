import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { CreateEventRequest } from '../../models/event.model';
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

// "2026-12-20T20:00" (champ datetime-local, heure locale) -> "2026-12-20T19:00:00.000Z" (Instant Java)
const toIso = (localDateTime: string): string => new Date(localDateTime).toISOString();

// Page /events/new : création d'un événement avec ses catégories (US1)
@Component({
  selector: 'app-event-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './event-form.html',
})
export class EventForm {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly eventService = inject(EventService);
  private readonly router = inject(Router);

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
    const request: CreateEventRequest = {
      title: value.title.trim(),
      description: value.description.trim() || null,
      venue: value.venue.trim(),
      startsAt: toIso(value.startsAt),
      salesStartAt: toIso(value.salesStartAt),
      categories: value.categories.map((c) => ({
        name: c.name.trim(),
        price: c.price,
        capacity: c.capacity,
      })),
    };

    this.eventService.create(request).subscribe({
      next: (created) => this.router.navigate(['/events', created.id]), // -> page détail
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
