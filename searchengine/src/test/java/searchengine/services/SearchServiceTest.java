package searchengine.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.dto.search.SearchResponse;
import searchengine.exceptions.indexing.SiteNotIndexedException;
import searchengine.exceptions.validation.InvalidQueryException;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SearchIndexRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.lemma.Lemmatizer;
import searchengine.services.search.SearchServiceImpl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {
    @Mock
    private LemmaRepository lemmaRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private SearchIndexRepository searchIndexRepository;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private Lemmatizer lemmatizer;
    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    public void searchReturnBadRequestEmptyQuery() {
        String emptyQuery = "   ";
        InvalidQueryException exception = assertThrows(InvalidQueryException.class, () -> {
            searchService.search(emptyQuery, "https://test.com", 0, 10);
        });
        assertEquals("Задан пустой поисковый запрос", exception.getMessage());
    }

    @Test
    public void searchReturnBadRequestSiteNotIndexed() {
        Site site = new Site();
        site.setStatus(SiteStatus.FAILED);
        when(siteRepository.findByUrl("https://test.com")).thenReturn(Optional.of(site));
        SiteNotIndexedException exception = assertThrows(SiteNotIndexedException.class, () -> {
            searchService.search("valid query", "https://test.com", 0, 10);
        });
        assertEquals("Указанный сайт не проиндексирован", exception.getMessage());
    }

    @Test
    public void searchReturnOkMinimalValid() {
        Site site = new Site();
        site.setId(1);
        site.setName("Test Site");
        site.setUrl("https://test.com");
        site.setStatus(SiteStatus.INDEXED);
        when(siteRepository.findByUrl("https://test.com")).thenReturn(Optional.of(site));
        when(lemmatizer.collectLemmas("тест")).thenReturn(Map.of("тест", 1));
        when(lemmaRepository.findBySiteAndLemmaIn(eq(site), anySet())).thenReturn(Collections.emptyList());
        SearchResponse response = searchService.search("тест", "https://test.com", 0, 10);
        assertTrue(response.isResult());
        assertInstanceOf(SearchResponse.class, response);
        assertEquals(0, response.getCount());
        assertTrue(response.getData().isEmpty());
    }
}