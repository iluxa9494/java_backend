package searchengine.dto.search;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для представления одного результата поиска.
 * Содержит информацию о сайте, странице, заголовке, сниппете и релевантности.
 */
@Setter
@Getter
public class SearchResult {
    private String uri;
    private String title;
    private String snippet;
    private float relevance;
    private String site;
    private String siteName;

    public SearchResult(String site, String siteName, String uri,
                        String title, String snippet, float relevance) {
        this.site = site;
        this.siteName = siteName;
        this.uri = uri;
        this.title = title;
        this.snippet = snippet;
        this.relevance = relevance;
    }
}