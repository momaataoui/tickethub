package ma.tickethub.backend.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Transforme toutes les exceptions en réponses ProblemDetail (RFC 9457).
 * Hérite de ResponseEntityExceptionHandler : les erreurs standard de Spring
 * (JSON mal formé, URL inconnue, mauvaise méthode HTTP...) ont déjà le bon code.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ---------- Erreurs métier ----------

    // 404 : la ressource demandée n'existe pas
    @ExceptionHandler({ EventNotFoundException.class, CategoryNotFoundException.class })
    public ProblemDetail handleNotFound(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 400 : la requête est incohérente
    @ExceptionHandler(InvalidEventDatesException.class)
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 403 : l'utilisateur n'a pas le droit (RG3)
    @ExceptionHandler(NotEventOwnerException.class)
    public ProblemDetail handleForbidden(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // 409 : la requête est valide, mais entre en conflit avec l'état actuel des données
    @ExceptionHandler({
            DuplicateCategoryNameException.class, // RG12
            EventCancelledException.class,
            InvalidCapacityException.class,       // RG8
            CategoryHasTicketsException.class,    // RG14
            LastCategoryException.class           // RG11
    })
    public ProblemDetail handleConflict(RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ---------- Validation (@Valid) : un message par champ ----------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage()); // ex : "title" -> "ne doit pas être vide"
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Données invalides");
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // ---------- Filet de sécurité de la base (contrainte unique, CHECK...) ----------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Contrainte de la base violée", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "L'opération viole une règle de la base de données");
    }

    // ---------- Tout le reste : 500, sans détail technique pour le client ----------

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Erreur inattendue", ex); // la stack trace reste dans les logs du serveur
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur inattendue est survenue");
    }
}
