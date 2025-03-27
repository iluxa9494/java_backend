package searchengine.services.search;

import searchengine.dto.search.SearchRequest;
import searchengine.dto.search.SearchResponse;

public interface SearchService {

    /**
     * Выполняет поиск по указанному запросу.
     *
     * @param searchRequest Запрос, содержащий текст для поиска и дополнительные параметры.
     * @return Результат поиска, включая список релевантных страниц.
     */
    SearchResponse search(SearchRequest searchRequest);
}
