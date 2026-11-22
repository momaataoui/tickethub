package ma.tickethub.backend.exception;

// Un événement annulé est définitif : ni modification, ni nouvelle annulation -> 409
public class EventCancelledException extends RuntimeException {

    public EventCancelledException(Long eventId) {
        super("L'événement " + eventId + " est annulé : il ne peut plus être modifié");
    }
}
