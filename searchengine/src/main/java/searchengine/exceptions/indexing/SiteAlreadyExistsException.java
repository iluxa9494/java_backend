package searchengine.exceptions.indexing;

public class SiteAlreadyExistsException extends RuntimeException {
    public SiteAlreadyExistsException(String message) {
        super(message);
    }
}