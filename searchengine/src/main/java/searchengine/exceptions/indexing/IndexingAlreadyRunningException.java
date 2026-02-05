package searchengine.exceptions.indexing;

public class IndexingAlreadyRunningException extends RuntimeException {
    public IndexingAlreadyRunningException(String message) {
        super(message);
    }
}