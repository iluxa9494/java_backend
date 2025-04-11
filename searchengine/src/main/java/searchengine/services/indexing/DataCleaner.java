package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;

import java.util.List;

/**
 * Сервис для очистки данных, связанных с сайтом: страниц, индексов и лемм.
 */
@Service
@RequiredArgsConstructor
public class DataCleaner {
    private final IndexRepository indexRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;

    @Transactional
    public void clearSiteData(Site site) {
        List<Page> pages = pageRepository.findBySite(site);
        if (!pages.isEmpty()) {
            indexRepository.deleteByPages(pages);
            pageRepository.deleteAll(pages);
        }
        lemmaRepository.deleteBySite(site);
    }
}