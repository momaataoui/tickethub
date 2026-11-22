package ma.tickethub.backend.exception;

public class EventNotFoundException extends RuntimeException {

    public EventNotFoundException(Long id) {
        super("Événement introuvable : " + id);
    }
}
