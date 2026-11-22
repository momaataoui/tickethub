// Format des erreurs renvoyées par le backend (GlobalExceptionHandler -> ProblemDetail, RFC 9457)
export interface ProblemDetail {
  status: number;
  title: string;
  detail: string;
  instance: string;
  errors?: Record<string, string>; // erreurs de validation : { "title": "ne doit pas être vide" }
}
