package searchengine.services.statistics;

import org.springframework.stereotype.Service;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    public StatisticsServiceImpl(SiteRepository siteRepository,
                                 PageRepository pageRepository,
                                 LemmaRepository lemmaRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public StatisticsResponse getStatistics() {
        long totalSites = siteRepository.count();
        long totalPages = pageRepository.count();
        long totalLemmas = lemmaRepository.count();
        boolean indexing = siteRepository.findAll().stream()
                .anyMatch(site -> "INDEXING".equals(site.getStatus()));
        List<DetailedStatisticsItem> detailedStats = new ArrayList<>();
        List<Site> sites = siteRepository.findAll();
        for (Site site : sites) {
            long pages = pageRepository.countBySite(site);
            long lemmas = lemmaRepository.countBySite(site);
            long statusTimeEpoch = site.getStatusTime().toEpochSecond(ZoneOffset.UTC);
            String statusTimeStr = String.valueOf(statusTimeEpoch);
            DetailedStatisticsItem item = new DetailedStatisticsItem(
                    site.getName(),
                    site.getUrl(),
                    site.getStatus(),
                    statusTimeStr,
                    site.getLastError(),
                    pages,
                    lemmas
            );
            detailedStats.add(item);
        }
        return new StatisticsResponse(totalPages, totalLemmas, detailedStats);
    }
}