package pl.kul.LibraryService.exception;

public class ResourceAlreadyExists extends RuntimeException{
    public ResourceAlreadyExists(String message) {
        super(message);
    }

    public ResourceAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}
