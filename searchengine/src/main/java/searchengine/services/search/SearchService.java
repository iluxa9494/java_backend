package searchengine.services.search;

import org.springframework.http.ResponseEntity;

/**
 * Сервис полнотекстового поиска по проиндексированным сайтам.
 */
public interface SearchService {
    ResponseEntity<?> search(String query, String site, int offset, int limit);
}