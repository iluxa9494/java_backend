package searchengine.exceptions.indexing;

public class IndexingNotRunningException extends RuntimeException {
    public IndexingNotRunningException(String message) {
        super(message);
    }
}