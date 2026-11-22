package ma.tickethub.backend.exception;

// RG11 : un événement garde toujours au moins une catégorie -> 409
public class LastCategoryException extends RuntimeException {

    public LastCategoryException() {
        super("Un événement doit garder au moins une catégorie");
    }
}
