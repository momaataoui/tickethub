package ma.tickethub.backend.exception;

// RG14 : on ne supprime pas une catégorie qui a déjà des billets vendus -> 409
public class CategoryHasTicketsException extends RuntimeException {

    public CategoryHasTicketsException(String name) {
        super("La catégorie « " + name + " » a déjà des billets vendus : suppression impossible");
    }
}
