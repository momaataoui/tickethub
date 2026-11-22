import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CreateEventRequest, EventResponse, UpdateEventRequest } from '../models/event.model';
import { Page } from '../models/page.model';

// Tous les appels HTTP liés aux événements, au même endroit.
// providedIn: 'root' -> Angular crée UNE seule instance partagée (comme un @Service Spring)
@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly http = inject(HttpClient); // injection de dépendances
  private readonly url = `${environment.apiUrl}/events`;

  // GET /api/events?page=0&size=10
  getAll(page = 0, size = 10): Observable<Page<EventResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<EventResponse>>(this.url, { params });
  }

  // GET /api/events/{id}
  getById(id: number): Observable<EventResponse> {
    return this.http.get<EventResponse>(`${this.url}/${id}`);
  }

  // POST /api/events
  create(request: CreateEventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(this.url, request);
  }

  // PUT /api/events/{id}
  update(id: number, request: UpdateEventRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.url}/${id}`, request);
  }

  // POST /api/events/{id}/cancel
  cancel(id: number): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.url}/${id}/cancel`, null);
  }
}
