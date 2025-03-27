package searchengine.services.indexing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.model.Site;
import searchengine.repositories.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static java.util.concurrent.ForkJoinTask.invokeAll;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final SiteRepository siteRepository;
    private final SitesList sitesList;

    public IndexingServiceImpl(SiteRepository siteRepository, SitesList sitesList) {
        this.siteRepository = siteRepository;
        this.sitesList = sitesList;
    }

    @Override
    public boolean startIndexing() {
        try {
            for (Site site : sitesList.getSites()) {
                site.setStatus("INDEXING");
                site.setStatusTime(LocalDateTime.now());
                siteRepository.save(site);

                Set<String> visitedUrls = new HashSet<>();

                ForkJoinPool pool = new ForkJoinPool();
                pool.invoke(new RecursiveAction() {
                    @Override
                    protected void compute() {
                        processPage(site, site.getUrl(), visitedUrls);
                    }
                });

                site.setStatus("INDEXED");
                site.setStatusTime(LocalDateTime.now());
                siteRepository.save(site);
            }
            return true;
        } catch (Exception e) {
            for (Site site : sitesList.getSites()) {
                site.setStatus("FAILED");
                site.setLastError(e.getMessage());
                site.setStatusTime(LocalDateTime.now());
                siteRepository.save(site);
            }
            return false;
        }
    }

    @Override
    public boolean stopIndexing() {
        return false;
    }

    @Override
    public boolean indexPage(String url) {
        return false;
    }

    @Override
    public boolean isIndexing() {
        return false;
    }

    @Override
    public boolean isDuplicateUrl(String url) {
        return false;
    }

    @Override
    public boolean addSite(String url, String name) {
        return false;
    }

    @Override
    public boolean siteExists(String url) {
        return siteRepository.findByUrl(url).isPresent();
    }

    private void processPage(Site site, String url, Set<String> visitedUrls) {
        if (visitedUrls.contains(url)) {
            return;
        }
        visitedUrls.add(url);
        try {
            Thread.sleep(randomDelay());
            Document doc = Jsoup.connect(url)
                    .userAgent("HeliontSearchBot")
                    .referrer("http://www.google.com")
                    .get();

            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);

            Elements linkElements = doc.select("a[href]");
            List<RecursiveAction> actions = new ArrayList<>();
            for (Element element : linkElements) {
                String link = element.attr("abs:href");
                if (link.startsWith(site.getUrl()) && !visitedUrls.contains(link)) {
                    actions.add(new RecursiveAction() {
                        @Override
                        protected void compute() {
                            processPage(site, link, visitedUrls);
                        }
                    });
                }
            }
            invokeAll(actions);
        } catch (Exception e) {
            System.err.println("Ошибка при обработке URL: " + url + ". " + e.getMessage());
        }
    }

    private int randomDelay() {
        return (int) (Math.random() * 4500) + 500;
    }
}