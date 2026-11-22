import { Component, computed, input } from '@angular/core';

import { EventStatus, SalesStatus } from '../../models/event.model';

// Badge réutilisable (liste + détail) : statut de la billetterie -> couleur + texte
@Component({
  selector: 'app-sales-status-badge',
  template: `<span class="badge" [class]="badge().css">{{ badge().label }}</span>`,
})
export class SalesStatusBadge {
  // input() : valeur passée par le composant parent, ex : <app-sales-status-badge [salesStatus]="...">
  readonly status = input.required<EventStatus>();
  readonly salesStatus = input.required<SalesStatus>();

  // computed() : recalculé automatiquement quand un input change
  protected readonly badge = computed(() => {
    if (this.status() === 'CANCELLED') {
      return { label: 'Annulé', css: 'text-bg-secondary' };
    }
    switch (this.salesStatus()) {
      case 'OPEN':
        return { label: 'Billetterie ouverte', css: 'text-bg-success' };
      case 'NOT_OPEN_YET':
        return { label: 'Bientôt en vente', css: 'text-bg-warning' };
      case 'SOLD_OUT':
        return { label: 'Complet', css: 'text-bg-danger' };
      case 'CLOSED':
        return { label: 'Billetterie fermée', css: 'text-bg-dark' };
    }
  });
}
