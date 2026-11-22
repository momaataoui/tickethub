package ma.tickethub.backend.exception;

public class InvalidEventDatesException extends RuntimeException {

    public InvalidEventDatesException() {
        super("La billetterie doit ouvrir avant le début de l'événement");
    }
}
