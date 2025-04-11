package searchengine.dto.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO-ответ на поисковый запрос.
 * Содержит флаг результата, количество найденных страниц и список найденных результатов.
 */
@Getter
@Setter
public class SearchResponse {
    private boolean result;
    private int count;
    private List<SearchResult> data;

    public SearchResponse(boolean result, int count, List<SearchResult> data) {
        this.result = result;
        this.count = count;
        this.data = data;
    }
}