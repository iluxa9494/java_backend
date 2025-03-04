//package searchengine.services;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import searchengine.dto.statistics.StatisticsResponse;
//import searchengine.model.Lemma;
//import searchengine.model.Page;
//import searchengine.model.Site;
//import searchengine.repositories.SiteRepository;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//public class StatisticsServiceTest {
//
//    @Mock
//    private SiteRepository siteRepository;
//
//    @InjectMocks
//    private StatisticsServiceImpl statisticsService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetStatisticsWithEmptyDatabase() {
//        when(siteRepository.findAll()).thenReturn(Collections.emptyList());
//        StatisticsResponse response = statisticsService.getStatistics();
//        assertNotNull(response);
//        assertEquals(0, response.getTotal().getPages());
//        assertEquals(0, response.getTotal().getLemmas());
//        assertTrue(response.getDetailed().isEmpty());
//    }
//
//    @Test
//    void testGetStatisticsWithMultipleSites() {
//        Site site1 = new Site(1, "Test Site 1", "https://testsite1.com", "INDEXED", LocalDateTime.now(), null);
//        Site site2 = new Site(2, "Test Site 2", "https://testsite2.com", "FAILED", LocalDateTime.now(), "Connection error");
//
//        site1.setPages(Arrays.asList(
//                new Page(1, "/page1", "Title 1", "<html>Content 1</html>", 200, site1),
//                new Page(2, "/page2", "Title 2", "<html>Content 2</html>", 200, site1)
//        ));
//        site1.setLemmas(Arrays.asList(
//                new Lemma(1, "lemma1", 10, site1),
//                new Lemma(2, "lemma2", 20, site1)
//        ));
//
//        site2.setPages(Collections.singletonList(
//                new Page(3, "/page3", "Title 3", "<html>Content 3</html>", 200, site2)
//        ));
//        site2.setLemmas(Collections.singletonList(
//                new Lemma(3, "lemma3", 30, site2)
//        ));
//
//        when(siteRepository.findAll()).thenReturn(Arrays.asList(site1, site2));
//        StatisticsResponse response = statisticsService.getStatistics();
//        assertNotNull(response);
//        assertEquals(3, response.getTotal().getPages());
//        assertEquals(3, response.getTotal().getLemmas());
//        assertEquals(2, response.getDetailed().size());
//        assertEquals("Test Site 1", response.getDetailed().get(0).getName());
//        assertEquals(2, response.getDetailed().get(0).getPages());
//        assertEquals(2, response.getDetailed().get(0).getLemmas());
//    }
//}