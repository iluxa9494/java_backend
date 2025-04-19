package searchengine.services.search;

import searchengine.dto.search.SearchResponse;

/**
 * Сервис полнотекстового поиска по проиндексированным сайтам.
 */
public interface SearchService {
    SearchResponse search(String query, String site, int offset, int limit);
}
