package searchengine.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.model.Site;
import searchengine.repositories.SiteRepository;
import searchengine.services.indexing.IndexingServiceImpl;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class IndexingServiceTest {
    private SiteRepository siteRepository;
    private IndexingServiceImpl indexingService;

    @BeforeEach
    public void setup() {
        siteRepository = mock(SiteRepository.class);
        indexingService = new IndexingServiceImpl(
                null,
                null,
                null,
                siteRepository,
                null,
                null,
                null
        );
    }

    @Test
    public void testIsUrlAllowedReturnTrueUrlMatches() {
        Site site = new Site();
        site.setUrl("https://example.com");
        when(siteRepository.findAll()).thenReturn(List.of(site));
        boolean result = indexingService.isUrlAllowed("https://example.com/page");
        assertTrue(result);
    }

    @Test
    public void testIsUrlAllowedReturnFalseUrlDoesNotMatch() {
        Site site = new Site();
        site.setUrl("https://example.com");
        when(siteRepository.findAll()).thenReturn(List.of(site));
        boolean result = indexingService.isUrlAllowed("https://other.com");
        assertFalse(result);
    }

    @Test
    public void testAddSiteAddNewSite() {
        when(siteRepository.findByUrl("https://example.com")).thenReturn(Optional.empty());
        boolean result = indexingService.addSite("https://example.com", "Example");
        assertTrue(result);
        verify(siteRepository).save(any());
    }

    @Test
    public void testAddSiteNotAddIfExists() {
        Site existing = new Site();
        when(siteRepository.findByUrl("https://example.com")).thenReturn(Optional.of(existing));
        boolean result = indexingService.addSite("https://example.com", "Example");
        assertFalse(result);
        verify(siteRepository, never()).save(any());
    }
}