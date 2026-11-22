// Format d'une page renvoyée par Spring (PagedModel) :
// { "content": [...], "page": { "size": 10, "number": 0, "totalElements": 4, "totalPages": 1 } }
export interface Page<T> {
  content: T[];
  page: {
    size: number;
    number: number; // numéro de la page courante (commence à 0)
    totalElements: number;
    totalPages: number;
  };
}
