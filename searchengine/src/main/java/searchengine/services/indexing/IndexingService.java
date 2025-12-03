package searchengine.services.indexing;

/**
 * Интерфейс сервиса для управления процессом индексации сайтов и отдельных страниц.
 */
public interface IndexingService {
    void startIndexing();

    void stopIndexing();

    void indexPage(String url);

    void addSite(String url, String name);
}