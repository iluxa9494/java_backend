package searchengine.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.exceptions.indexing.SiteAlreadyExistsException;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SearchIndexRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.indexing.DataCleaner;
import searchengine.services.indexing.IndexingServiceImpl;
import searchengine.services.lemma.Lemmatizer;
import searchengine.services.site.SiteStatusService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class IndexingServiceTest {
    private SiteRepository siteRepository;
    private LemmaRepository lemmaRepository;
    private SearchIndexRepository searchIndexRepository;
    private PageRepository pageRepository;
    private SiteStatusService siteStatusService;
    private DataCleaner dataCleaner;
    private UrlValidationService urlValidationService;
    private IndexingServiceImpl indexingService;

    @BeforeEach
    public void setup() {
        siteRepository = mock(SiteRepository.class);
        lemmaRepository = mock(LemmaRepository.class);
        searchIndexRepository = mock(SearchIndexRepository.class);
        pageRepository = mock(PageRepository.class);
        siteStatusService = mock(SiteStatusService.class);
        dataCleaner = mock(DataCleaner.class);
        urlValidationService = mock(UrlValidationService.class);
        indexingService = new IndexingServiceImpl(
                mock(Lemmatizer.class),
                lemmaRepository,
                searchIndexRepository,
                siteRepository,
                pageRepository,
                siteStatusService,
                dataCleaner,
                urlValidationService
        );
    }

    @Test
    public void testAddSiteAddNewSite() {
        when(siteRepository.findByUrl("https://example.com")).thenReturn(Optional.empty());
        indexingService.addSite("https://example.com", "Example");
        verify(siteRepository).save(any());
    }

    @Test
    public void testAddSiteNotAddIfExists() {
        Site existing = new Site();
        existing.setUrl("https://example.com");
        when(siteRepository.findByUrl("https://example.com")).thenReturn(Optional.of(existing));
        try {
            indexingService.addSite("https://example.com", "Example");
        } catch (SiteAlreadyExistsException e) {
        }
        verify(siteRepository, never()).save(any());
    }

    @Test
    public void testSiteExists() {
        Site site = new Site();
        site.setUrl("https://example.com");
        when(siteRepository.findByUrl("https://example.com")).thenReturn(Optional.of(site));
        Optional<Site> result = siteRepository.findByUrl("https://example.com");
        assertTrue(result.isPresent());
    }

    @Test
    public void testSiteDoesNotExist() {
        when(siteRepository.findByUrl("https://other.com")).thenReturn(Optional.empty());
        Optional<Site> result = siteRepository.findByUrl("https://other.com");
        assertFalse(result.isPresent());
    }
}