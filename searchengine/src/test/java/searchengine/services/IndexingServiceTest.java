//package searchengine.services;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import searchengine.model.Page;
//import searchengine.model.Site;
//import searchengine.repositories.IndexRepository;
//import searchengine.repositories.LemmaRepository;
//import searchengine.repositories.PageRepository;
//import searchengine.repositories.SiteRepository;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//class IndexingServiceTest {
//
//    @InjectMocks
//    private IndexingServiceImpl indexingService;
//
//    @Mock
//    private SiteRepository siteRepository;
//
//    @Mock
//    private PageRepository pageRepository;
//
//    @Mock
//    private LemmaRepository lemmaRepository;
//
//    @Mock
//    private IndexRepository indexRepository;
//
//    private Site mockSite;
//    private Page mockPage;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockSite = new Site();
//        mockSite.setId(1);
//        mockSite.setUrl("https://example.com");
//
//        mockPage = new Page();
//        mockPage.setId(1);
//        mockPage.setSite(mockSite);
//        mockPage.setPath("/path");
//    }
//
//    @Test
//    void testStartIndexing() {
//        when(siteRepository.findAll()).thenReturn(List.of(mockSite));
//
//        boolean result = indexingService.startIndexing();
//
//        assertTrue(result);
//        verify(siteRepository, times(1)).findAll();
//    }
//
//    @Test
//    void testStartIndexingWhenAlreadyRunning() {
//        when(siteRepository.findAll()).thenReturn(List.of(mockSite));
//
//        indexingService.startIndexing();
//        boolean result = indexingService.startIndexing();
//
//        assertFalse(result);
//        verify(siteRepository, times(1)).findAll();
//    }
//
//    @Test
//    void testStopIndexing() {
//        when(siteRepository.findAll()).thenReturn(List.of(mockSite));
//        indexingService.startIndexing();
//
//        boolean result = indexingService.stopIndexing();
//
//        assertTrue(result);
//        verifyNoMoreInteractions(siteRepository, pageRepository, lemmaRepository, indexRepository);
//    }
//
//    @Test
//    void testIndexPage() {
//        when(siteRepository.findAll()).thenReturn(List.of(mockSite));
//        when(pageRepository.save(any())).thenReturn(mockPage);
//
//        boolean result = indexingService.indexPage("https://example.com/path");
//
//        assertTrue(result);
//        verify(pageRepository, times(1)).save(any());
//    }
//
//    @Test
//    void testLemmaSavingDuringIndexing() {
//        when(siteRepository.findAll()).thenReturn(List.of(mockSite));
//        when(pageRepository.save(any())).thenReturn(mockPage);
//
//        indexingService.startIndexing();
//
//        verify(lemmaRepository, atLeastOnce()).save(any());
//        verify(indexRepository, atLeastOnce()).save(any());
//    }
//
//    @Test
//    void testStopIndexingWhenNotRunning() {
//        boolean result = indexingService.stopIndexing();
//
//        assertFalse(result);
//    }
//}
