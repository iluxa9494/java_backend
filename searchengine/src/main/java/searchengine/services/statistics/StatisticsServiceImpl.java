package searchengine.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.FinalStatisticsResponse;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис статистики - формирует сводную и детализированную информацию по индексированным сайтам.
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Override
    public FinalStatisticsResponse getStatistics() {
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        long totalPages = 0;
        long totalLemmas = 0;
        boolean isIndexing = false;
        List<Site> sites = siteRepository.findAll();
        for (Site site : sites) {
            long pages = pageRepository.countBySite(site);
            long lemmas = lemmaRepository.countBySite(site);
            totalPages += pages;
            totalLemmas += lemmas;
            long statusTime = site.getStatusTime() != null
                    ? site.getStatusTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    : 0L;
            detailed.add(new DetailedStatisticsItem(
                    site.getName(),
                    site.getUrl(),
                    site.getStatus().name(),
                    site.getLastError(),
                    statusTime,
                    pages,
                    lemmas
            ));
            if (site.getStatus().name().equals("INDEXING")) {
                isIndexing = true;
            }
        }
        TotalStatistics total = new TotalStatistics(
                sites.size(), totalPages, totalLemmas, isIndexing);
        StatisticsData data = new StatisticsData(total, detailed);
        return new FinalStatisticsResponse(true, data);
    }
}