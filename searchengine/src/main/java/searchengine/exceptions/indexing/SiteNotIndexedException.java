package searchengine.exceptions.indexing;

/**
 * Исключение, которое выбрасывается, когда сайт не был проиндексирован.
 * Наследуется от {@link RuntimeException}.
 */
public class SiteNotIndexedException extends RuntimeException {
    public SiteNotIndexedException(String message) {
        super(message);
    }
}