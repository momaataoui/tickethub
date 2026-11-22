// Copie TypeScript des DTO Java (package dto du backend).
// Si un DTO Java change, ce fichier doit changer aussi.

// ---------- Enums (côté Java : entity/*Status.java) ----------

export type EventStatus = 'PUBLISHED' | 'CANCELLED';
export type SalesStatus = 'NOT_OPEN_YET' | 'OPEN' | 'SOLD_OUT' | 'CLOSED';
export type CategoryStatus = 'AVAILABLE' | 'SOLD_OUT';

// ---------- Réponses (ce que l'API renvoie) ----------

export interface CategoryResponse {
  id: number;
  name: string;
  price: number;
  capacity: number;
  availableSeats: number;
  status: CategoryStatus;
}

export interface EventResponse {
  id: number;
  title: string;
  description: string | null;
  venue: string;
  startsAt: string; // Instant Java -> date ISO, ex : "2026-12-20T20:00:00Z"
  salesStartAt: string;
  status: EventStatus;
  salesStatus: SalesStatus;
  minPrice: number | null; // "à partir de X DH"
  totalCapacity: number;
  availableSeats: number;
  organizerName: string;
  categories: CategoryResponse[];
}

// ---------- Requêtes (ce que l'API reçoit) ----------

export interface CategoryRequest {
  name: string;
  price: number;
  capacity: number;
}

export interface CreateEventRequest {
  title: string;
  description: string | null;
  venue: string;
  startsAt: string;
  salesStartAt: string;
  categories: CategoryRequest[];
}

export interface UpdateEventRequest {
  title: string;
  description: string | null;
  venue: string;
  startsAt: string;
  salesStartAt: string;
}
