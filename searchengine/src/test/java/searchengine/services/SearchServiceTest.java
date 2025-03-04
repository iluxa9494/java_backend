//package searchengine.services;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import searchengine.dto.search.SearchRequest;
//import searchengine.dto.search.SearchResponse;
//import searchengine.dto.search.SearchResult;
//import searchengine.model.Index;
//import searchengine.model.Lemma;
//import searchengine.model.Page;
//import searchengine.model.Site;
//import searchengine.repositories.IndexRepository;
//import searchengine.repositories.LemmaRepository;
//import searchengine.repositories.PageRepository;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//public class SearchServiceTest {
//
//    @Mock
//    private LemmaRepository lemmaRepository;
//
//    @Mock
//    private IndexRepository indexRepository;
//
//    @Mock
//    private PageRepository pageRepository;
//
//    @InjectMocks
//    private SearchServiceImpl searchService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSearchWithValidQuery() {
//        String query = "тест";
//        SearchRequest request = new SearchRequest(query, null);
//
//        Site site = new Site(1, "Test Site", "https://testsite.com", "INDEXED", LocalDateTime.now(), null);
//        Lemma lemma = new Lemma(1, "тест", 10, site);
//        Page page = new Page(1, "/test", "Test Title", "<html>Content тест</html>", 200, site);
//        Index index = new Index(1, page, lemma, 1.5f);
//
//        when(lemmaRepository.findByLemma("тест")).thenReturn(lemma);
//        when(indexRepository.findByLemmaIdIn(Collections.singletonList(1)))
//                .thenReturn(Collections.singletonList(index));
//        when(pageRepository.findAllById(Collections.singleton(1)))
//                .thenReturn(Collections.singletonList(page));
//        SearchResponse response = searchService.search(request);
//        assertNotNull(response);
//        assertEquals(1, response.getResultCount());
//        List<SearchResult> results = response.getData();
//        assertEquals(1, results.size());
//        assertEquals("/test", results.get(0).getUri());
//        assertEquals("Test Title", results.get(0).getTitle());
//        assertTrue(results.get(0).getSnippet().contains("тест"));
//        assertEquals(1.5f, results.get(0).getRelevance());
//    }
//
//    @Test
//    void testSearchWithEmptyQuery() {
//        SearchRequest request = new SearchRequest("", null);
//        SearchResponse response = searchService.search(request);
//        assertNotNull(response);
//        assertEquals(0, response.getResultCount());
//        assertTrue(response.getData().isEmpty());
//    }
//
//    @Test
//    void testSearchWithNoResults() {
//        String query = "нетрезультатов";
//        SearchRequest request = new SearchRequest(query, null);
//
//        when(lemmaRepository.findByLemma(query)).thenReturn(null);
//        SearchResponse response = searchService.search(request);
//        assertNotNull(response);
//        assertEquals(0, response.getResultCount());
//        assertTrue(response.getData().isEmpty());
//    }
//}
