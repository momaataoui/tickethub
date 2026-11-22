import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, input, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SalesStatusBadge } from '../../components/sales-status-badge/sales-status-badge';
import { EventResponse } from '../../models/event.model';
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

  // Pourcentage de places vendues, pour la barre de progression
  protected soldPercent(capacity: number, availableSeats: number): number {
    return Math.round(((capacity - availableSeats) / capacity) * 100);
  }
}
