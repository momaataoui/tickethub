import { Routes } from '@angular/router';

import { EventDetail } from './pages/event-detail/event-detail';
import { EventForm } from './pages/event-form/event-form';
import { EventList } from './pages/event-list/event-list';

// Table des pages : URL -> composant.
// L'ORDRE compte : Angular prend la première route qui correspond.
export const routes: Routes = [
  { path: '', redirectTo: 'events', pathMatch: 'full' },
  { path: 'events', component: EventList, title: 'Événements | TicketHub' },
  { path: 'events/new', component: EventForm, title: 'Créer un événement | TicketHub' }, // AVANT :id
  { path: 'events/:id', component: EventDetail, title: 'Détail | TicketHub' }, // :id -> input "id"
  { path: 'events/:id/edit', component: EventForm, title: 'Modifier | TicketHub' }, // même formulaire
  { path: '**', redirectTo: 'events' }, // URL inconnue -> retour à la liste
];
