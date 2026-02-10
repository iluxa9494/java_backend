package searchengine.exceptions.indexing;

/**
 * Исключение выбрасывается, когда попытка остановить или запустить индексацию
 * невозможна из-за текущего состояния (например, не запущена или уже идёт).
 */
public class IndexingNotRunningException extends RuntimeException {
    public IndexingNotRunningException(String message) {
        super(message);
    }
}