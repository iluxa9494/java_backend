package searchengine.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import searchengine.dto.search.ErrorResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.lemma.Lemmatizer;
import searchengine.services.search.SearchServiceImpl;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class SearchServiceTest {
    private SearchServiceImpl searchService;
    private Lemmatizer lemmatizer;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;
    private PageRepository pageRepository;
    private SiteRepository siteRepository;

    @BeforeEach
    public void setup() {
        lemmatizer = mock(Lemmatizer.class);
        lemmaRepository = mock(LemmaRepository.class);
        indexRepository = mock(IndexRepository.class);
        pageRepository = mock(PageRepository.class);
        siteRepository = mock(SiteRepository.class);
        searchService = new SearchServiceImpl(
                lemmatizer,
                lemmaRepository,
                indexRepository,
                pageRepository,
                siteRepository
        );
    }

    @Test
    public void searchReturnBadRequestEmptyQuery() {
        ResponseEntity<?> response = searchService.search("   ", null, 0, 10);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    public void searchReturnBadRequestSiteNotIndexed() {
        when(siteRepository.findByUrl("https://test.com")).thenReturn(Optional.of(new Site()));
        ResponseEntity<?> response = searchService.search("тест", "https://test.com", 0, 10);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void searchReturnBadRequestLemmasNotFound() {
        Site site = new Site();
        site.setStatus(SiteStatus.INDEXED);
        when(siteRepository.findByUrl("https://test.com")).thenReturn(Optional.of(site));
        when(lemmatizer.collectLemmas("тест")).thenReturn(Collections.emptyMap());
        ResponseEntity<?> response = searchService.search("тест", "https://test.com", 0, 10);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void searchReturnOkMinimalValid() {
        Site site = new Site();
        site.setId(1);
        site.setName("Test");
        site.setUrl("https://test.com");
        site.setStatus(SiteStatus.INDEXED);
        when(siteRepository.findByUrl("https://test.com")).thenReturn(Optional.of(site));
        when(lemmatizer.collectLemmas("тест")).thenReturn(Map.of("тест", 1));
        when(lemmaRepository.findBySiteAndLemmaIn(eq(site), anySet())).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = searchService.search("тест", "https://test.com", 0, 10);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof SearchResponse);
    }
}