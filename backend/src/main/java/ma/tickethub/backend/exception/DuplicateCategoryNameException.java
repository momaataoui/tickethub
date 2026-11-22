package ma.tickethub.backend.exception;

public class DuplicateCategoryNameException extends RuntimeException {

    public DuplicateCategoryNameException(String name) {
        super("La catégorie « " + name + " » existe déjà pour cet événement");
    }
}
