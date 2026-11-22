package ma.tickethub.backend.exception;

// RG8 : la capacité ne peut pas descendre sous les places déjà vendues -> 409
public class InvalidCapacityException extends RuntimeException {

    public InvalidCapacityException(int capacity, int soldSeats) {
        super("Capacité " + capacity + " impossible : " + soldSeats + " places sont déjà vendues");
    }
}
