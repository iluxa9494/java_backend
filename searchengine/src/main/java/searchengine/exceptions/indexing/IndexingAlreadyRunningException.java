package searchengine.exceptions.indexing;

/**
 * Исключение выбрасывается, когда попытка начать индексацию повторно,
 * в то время как она уже выполняется.
 */
public class IndexingAlreadyRunningException extends RuntimeException {
    public IndexingAlreadyRunningException(String message) {
        super(message);
    }
}