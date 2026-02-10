package searchengine.exceptions.validation;

/**
 * Исключение, которое выбрасывается в случае некорректного запроса.
 * Наследуется от {@link RuntimeException}.
 */
public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String message) {
        super(message);
    }
}