package searchengine.dto.search;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRequest {
    private String query;
    private String site;

    public SearchRequest() {
    }

    public SearchRequest(String query, String site) {
        this.query = query;
        this.site = site;
    }
}