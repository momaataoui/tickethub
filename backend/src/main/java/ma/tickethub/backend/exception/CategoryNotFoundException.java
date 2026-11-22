package ma.tickethub.backend.exception;

// La catégorie n'existe pas, ou n'appartient pas à cet événement -> 404
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super("Catégorie introuvable pour cet événement : " + categoryId);
    }
}
