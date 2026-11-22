import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SalesStatusBadge } from '../../components/sales-status-badge/sales-status-badge';
import { EventResponse } from '../../models/event.model';
import { ProblemDetail } from '../../models/problem-detail.model';
import { EventService } from '../../services/event.service';

// Page /events/:id : détail d'un événement et de ses catégories (US3)
@Component({
  selector: 'app-event-detail',
  imports: [DatePipe, DecimalPipe, RouterLink, SalesStatusBadge],
  templateUrl: './event-detail.html',
})
export class EventDetail implements OnInit {
  private readonly eventService = inject(EventService);

  // Rempli automatiquement avec le ":id" de l'URL (withComponentInputBinding) = @PathVariable
  readonly id = input.required<string>();

  protected readonly event = signal<EventResponse | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly cancelling = signal(false);
  protected readonly actionError = signal<string | null>(null);

  ngOnInit(): void {
    this.eventService.getById(Number(this.id())).subscribe({
      next: (event) => {
        this.event.set(event);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        // 404 -> message clair ; sinon -> problème de connexion
        this.error.set(
          err.status === 404
            ? 'Événement introuvable.'
            : 'Impossible de charger l\'événement. Le backend est-il démarré ?',
        );
        this.loading.set(false);
      },
    });
  }

  // US6 : annuler l'événement (définitif -> on demande confirmation)
  protected cancelEvent(event: EventResponse): void {
    if (!confirm(`Annuler « ${event.title} » ? Cette action est définitive.`)) {
      return;
    }
    this.cancelling.set(true);
    this.actionError.set(null);
    this.eventService.cancel(event.id).subscribe({
      next: (updated) => {
        this.event.set(updated); // badge "Annulé" + boutons masqués, sans recharger la page
        this.cancelling.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.actionError.set((err.error as ProblemDetail | null)?.detail ?? 'L\'annulation a échoué.');
        this.cancelling.set(false);
      },
    });
  }

  // Pourcentage de places vendues, pour la barre de progression
  protected soldPercent(capacity: number, availableSeats: number): number {
    return Math.round(((capacity - availableSeats) / capacity) * 100);
  }
}
