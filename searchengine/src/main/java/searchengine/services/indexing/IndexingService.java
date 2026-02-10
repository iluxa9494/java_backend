package searchengine.services.indexing;

public interface IndexingService {
    void startIndexing();

    void stopIndexing();

    void indexPage(String url);

    void addSite(String url, String name);
}