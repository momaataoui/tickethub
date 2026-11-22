package ma.tickethub.backend.exception;

// RG3 : un organisateur ne modifie ou n'annule que ses propres événements -> 403
public class NotEventOwnerException extends RuntimeException {

    public NotEventOwnerException(Long eventId) {
        super("Vous n'êtes pas l'organisateur de l'événement " + eventId);
    }
}
