package searchengine.exceptions.validation;

/**
 * Исключение, которое выбрасывается в случае некорректного URL.
 * Наследуется от {@link RuntimeException}.
 */
public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String message) {
        super(message);
    }
}