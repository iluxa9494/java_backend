package searchengine.exceptions.general;

public class UrlNotAllowedException extends RuntimeException {
    public UrlNotAllowedException(String message) {
        super(message);
    }
}