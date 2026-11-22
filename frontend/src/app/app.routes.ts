import { Routes } from '@angular/router';

import { EventList } from './pages/event-list/event-list';

// Table des pages : URL -> composant
export const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
  { path: 'events', component: EventList, title: 'Événements | TicketHub' },
  { path: '**', redirectTo: 'events' }, // URL inconnue -> retour à la liste
];
