package searchengine.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.dto.statistics.FinalStatisticsResponse;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.statistics.StatisticsServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatisticsServiceTest {
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private StatisticsServiceImpl statisticsService;

    @BeforeEach
    public void setup() {
        siteRepository = mock(SiteRepository.class);
        pageRepository = mock(PageRepository.class);
        lemmaRepository = mock(LemmaRepository.class);
        statisticsService = new StatisticsServiceImpl(siteRepository, pageRepository, lemmaRepository);
    }

    @Test
    public void getStatisticsReturnZeroOnEmptyData() {
        when(siteRepository.findAll()).thenReturn(List.of());
        FinalStatisticsResponse response = statisticsService.getStatistics();
        assertTrue(response.isResult());
        StatisticsData data = response.getStatistics();
        TotalStatistics total = data.getTotal();
        assertEquals(0, total.getSites());
        assertEquals(0, total.getPages());
        assertEquals(0, total.getLemmas());
        assertFalse(total.isIndexing());
        assertTrue(data.getDetailed().isEmpty());
    }

    @Test
    public void getStatisticsReturnCorrectDataForOneSite() {
        Site site = new Site();
        site.setName("Test");
        site.setUrl("https://test.com");
        site.setStatus(SiteStatus.INDEXED);
        site.setStatusTime(LocalDateTime.now());
        when(siteRepository.findAll()).thenReturn(List.of(site));
        when(pageRepository.countBySite(site)).thenReturn(5L);
        when(lemmaRepository.countBySite(site)).thenReturn(10L);
        FinalStatisticsResponse response = statisticsService.getStatistics();
        assertTrue(response.isResult());
        StatisticsData data = response.getStatistics();
        TotalStatistics total = data.getTotal();
        assertEquals(1, total.getSites());
        assertEquals(5, total.getPages());
        assertEquals(10, total.getLemmas());
        assertFalse(total.isIndexing());
        assertEquals(1, data.getDetailed().size());
    }

    @Test
    public void getStatisticsSetIndexingTrueIfAnySiteIsIndexing() {
        Site site = new Site();
        site.setName("IndexingSite");
        site.setUrl("https://indexing.com");
        site.setStatus(SiteStatus.INDEXING);
        site.setStatusTime(LocalDateTime.now());
        when(siteRepository.findAll()).thenReturn(List.of(site));
        when(pageRepository.countBySite(site)).thenReturn(3L);
        when(lemmaRepository.countBySite(site)).thenReturn(7L);
        FinalStatisticsResponse response = statisticsService.getStatistics();
        assertTrue(response.getStatistics().getTotal().isIndexing());
    }
}