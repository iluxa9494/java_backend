package searchengine.services.indexing;

/**
 * Интерфейс сервиса для управления процессом индексации сайтов и отдельных страниц.
 */
public interface IndexingService {
    boolean startIndexing();
    boolean stopIndexing();
    boolean indexPage(String url);
    boolean addSite(String url, String name);

    boolean isUrlAllowed(String url);
}