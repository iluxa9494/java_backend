package searchengine.dto.search;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class SearchResponse {
    private int count;
    private List<SearchResult> data;

    public SearchResponse() {
    }

    public SearchResponse(int count, List<SearchResult> data) {
        this.count = count;
        this.data = data;
    }
}