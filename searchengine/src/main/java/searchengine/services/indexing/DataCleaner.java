package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SearchIndexRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataCleaner {
    private final SearchIndexRepository searchIndexRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Transactional
    public void clearSiteData(Site site) {
        List<Page> pages = pageRepository.findBySite(site);
        if (!pages.isEmpty()) {
            searchIndexRepository.deleteByPages(pages);
            pageRepository.deleteAll(pages);
        }
        lemmaRepository.deleteBySite(site);
    }
}