package searchengine.exceptions.general;

/**
 * Исключение, которое выбрасывается, когда URL не разрешён для индексации.
 * Наследуется от {@link RuntimeException}.
 */
public class UrlNotAllowedException extends RuntimeException {
    public UrlNotAllowedException(String message) {
        super(message);
    }
}