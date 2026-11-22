import { Routes } from '@angular/router';

import { EventDetail } from './pages/event-detail/event-detail';
import { EventList } from './pages/event-list/event-list';

// Table des pages : URL -> composant
export const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
  { path: 'events', component: EventList, title: 'Événements | TicketHub' },
  { path: 'events/:id', component: EventDetail, title: 'Détail | TicketHub' }, // :id -> input "id"
  { path: '**', redirectTo: 'events' }, // URL inconnue -> retour à la liste
];
