package searchengine.exceptions.indexing;

/**
 * Исключение, которое выбрасывается, когда сайт уже существует в базе данных.
 * Наследуется от {@link RuntimeException}.
 */
public class SiteAlreadyExistsException extends RuntimeException {
    public SiteAlreadyExistsException(String message) {
        super(message);
    }
}