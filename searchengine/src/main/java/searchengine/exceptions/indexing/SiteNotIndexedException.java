package searchengine.exceptions.indexing;

public class SiteNotIndexedException extends RuntimeException {
    public SiteNotIndexedException(String message) {
        super(message);
    }
}