import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { SalesStatusBadge } from '../../components/sales-status-badge/sales-status-badge';
import { EventResponse } from '../../models/event.model';
import { Page } from '../../models/page.model';
import { EventService } from '../../services/event.service';

// Page /events : liste paginée des événements (US2)
@Component({
  selector: 'app-event-list',
  imports: [DatePipe, DecimalPipe, RouterLink, SalesStatusBadge],
  templateUrl: './event-list.html',
})
export class EventList implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly pageSize = 6; // 2 lignes de 3 cartes

  // signals : quand leur valeur change, Angular met l'affichage à jour (indispensable en zoneless)
  protected readonly page = signal<Page<EventResponse> | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  // ngOnInit : appelé une fois, quand le composant s'affiche
  ngOnInit(): void {
    this.loadPage(0);
  }

  protected loadPage(pageNumber: number): void {
    this.loading.set(true);
    this.error.set(null);

    // Sans subscribe(), la requête HTTP n'est jamais envoyée
    this.eventService.getAll(pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.page.set(page);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les événements. Le backend est-il démarré ?');
        this.loading.set(false);
      },
    });
  }
}
