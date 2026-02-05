package searchengine.services.site;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;
import searchengine.model.SiteStatus;
import searchengine.repositories.SiteRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SiteStatusService {
    private final SiteRepository siteRepository;

    public SiteStatusService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSitesAsIndexing(List<Site> sites) {
        for (Site site : sites) {
            site.setStatus(SiteStatus.INDEXING);
            site.setLastError(null);
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
        }
    }
}