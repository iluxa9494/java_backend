package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.lemma.Lemmatizer;
import searchengine.services.site.SiteStatusService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Реализация сервиса индексации, выполняющая индексацию сайтов и отдельных страниц,
 * а также управление процессом запуска и остановки индексации.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private final Lemmatizer lemmatizer;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final SiteStatusService siteStatusService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final DataCleaner dataCleaner;
    private volatile boolean stopFlag = false;

    @Override
    public boolean addSite(String url, String name) {
        if (siteRepository.findByUrl(url).isPresent()) return false;
        Site site = new Site();
        site.setUrl(url);
        site.setName(name);
        site.setStatus(SiteStatus.INDEXED);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
        return true;
    }

    @Override
    @Transactional
    public boolean startIndexing() {
        if (siteRepository.findByStatus(SiteStatus.INDEXING).size() > 0) {
            log.warn("Индексация уже запущена");
            return false;
        }
        stopFlag = false;
        List<Site> sitesToIndex = siteRepository.findAll().stream().filter(site -> site.getStatus() == SiteStatus.INDEXED || site.getStatus() == SiteStatus.FAILED).toList();
        if (sitesToIndex.isEmpty()) {
            log.warn("Нет сайтов со статусом INDEXED или FAILED для индексации");
            return false;
        }
        siteStatusService.markSitesAsIndexing(sitesToIndex);
        for (Site site : sitesToIndex) {
            executorService.submit(() -> indexSingleSite(site));
        }
        log.info("Отправлено {} задач(и) на индексацию в ExecutorService", sitesToIndex.size());
        return true;
    }

    public void indexSingleSite(Site site) {
        if (stopFlag) {
            log.info("Индексация сайта '{}' остановлена до начала обработки", site.getUrl());
            return;
        }
        log.info("Индексация начата: {}", site.getUrl());
        try {
            Optional<String> availabilityError = getSiteAvailabilityError(site.getUrl());
            if (availabilityError.isPresent()) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError(availabilityError.get());
                site.setStatusTime(LocalDateTime.now());
                siteRepository.save(site);
                log.warn("Пропуск сайта {}: {}", site.getUrl(), availabilityError.get());
                return;
            }
            dataCleaner.clearSiteData(site);
            if (stopFlag) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Индексация остановлена вручную");
                site.setStatusTime(LocalDateTime.now());
                siteRepository.save(site);
                log.info("Индексация прервана после очистки данных: {}", site.getUrl());
                return;
            }
            boolean success = indexPage(site.getUrl());
            if (stopFlag) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Индексация остановлена вручную");
                log.warn("Индексация прервана в момент парсинга сайта: {}", site.getUrl());
            } else if (success) {
                site.setStatus(SiteStatus.INDEXED);
                site.setLastError(null);
                log.info("Сайт успешно проиндексирован: {}", site.getUrl());
            } else {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Ошибка при индексации");
                log.warn("Ошибка при индексации сайта: {}", site.getUrl());
            }
        } catch (Exception e) {
            site.setStatus(SiteStatus.FAILED);
            site.setLastError("Индексация завершилась ошибкой: " + e.getMessage());
            log.error("Ошибка при индексации {}: {}", site.getUrl(), e.getMessage(), e);
        } finally {
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
            log.info("Индексация завершена: {}", site.getUrl());
        }
    }

    @Override
    @Transactional
    public boolean stopIndexing() {
        List<Site> indexingSites = siteRepository.findByStatus(SiteStatus.INDEXING);
        if (indexingSites.isEmpty()) {
            log.warn("Индексация не запущена — нет сайтов со статусом INDEXING.");
            return false;
        }
        stopFlag = true;
        for (Site site : indexingSites) {
            site.setStatus(SiteStatus.FAILED);
            site.setLastError("Индексация остановлена вручную");
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
            log.info("Сайт принудительно переведён в FAILED: {}", site.getUrl());
        }
        return true;
    }

    @Override
    public boolean indexPage(String url) {
        String siteUrl = normalizeUrl(url);
        String path;
        try {
            path = new URL(url).getPath();
            if (path.isEmpty()) path = "/";
            if (!path.startsWith("/")) path = "/" + path;
        } catch (MalformedURLException e) {
            log.error("Некорректный URL: {}", url, e);
            return false;
        }
        Optional<Site> optSite = siteRepository.findByUrlIgnoreWww(siteUrl);
        if (optSite.isEmpty() && !siteUrl.contains("www.")) {
            String altUrl = siteUrl.replace("://", "://www.");
            log.debug("Попытка найти сайт по альтернативному URL: {}", altUrl);
            optSite = siteRepository.findByUrl(altUrl);
        }
        if (optSite.isEmpty()) {
            log.warn("Сайт не найден: {}", siteUrl);
            return false;
        }
        Site site = optSite.get();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .referrer("https://google.com")
                    .timeout(10000)
                    .get();
            String content = doc.html();
            String title = Optional.ofNullable(doc.title())
                    .filter(t -> !t.isBlank())
                    .orElse("(no title)");
            Page existing = pageRepository.findBySiteAndPath(site, path);
            if (existing != null) {
                indexRepository.deleteAll(indexRepository.findByPage(existing));
                pageRepository.delete(existing);
            }
            Page page = new Page();
            page.setSite(site);
            page.setPath(path);
            page.setCode(200);
            page.setTitle(title);
            page.setContent(content);
            page = pageRepository.save(page);
            Map<String, Integer> lemmas = lemmatizer.collectLemmas(
                    Jsoup.clean(doc.body().html(), Safelist.none()).trim()
            );
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemmaText = entry.getKey();
                int freq = entry.getValue();
                Lemma lemma = lemmaRepository.findByLemmaAndSiteId(lemmaText, site.getId());
                if (lemma == null) lemma = new Lemma(lemmaText, 0, site);
                lemma.setFrequency(lemma.getFrequency() + 1);
                lemmaRepository.save(lemma);
                Index index = new Index();
                index.setPage(page);
                index.setLemma(lemma);
                index.setRank(freq);
                indexRepository.save(index);
            }
            log.info("Страница проиндексирована: {}", url);
            return true;
        } catch (IOException e) {
            log.error("Ошибка при загрузке страницы {}: {}", url, e.getMessage());
            return false;
        }
    }

    @PreDestroy
    public void shutdownExecutor() {
        log.info("Завершаем ExecutorService...");
        executorService.shutdownNow();
    }

    @PostConstruct
    public void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdownNow();
            log.info("ExecutorService завершён через shutdown hook (JVM shutdown)");
        }));
    }

    private Optional<String> getSiteAvailabilityError(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(false);
            int code = conn.getResponseCode();
            String message = conn.getResponseMessage();
            if (code >= 200 && code < 400) {
                return Optional.empty();
            } else {
                return Optional.of("Сайт недоступен, " + code + " " + message);
            }
        } catch (SSLHandshakeException e) {
            return Optional.of("Сайт недоступен, SSL error: " + e.getMessage());
        } catch (IOException e) {
            return Optional.of("Сайт недоступен, ошибка: " + e.getMessage());
        }
    }

    private String normalizeUrl(String url) {
        try {
            URL u = new URL(url);
            String protocol = u.getProtocol();
            String host = u.getHost().toLowerCase().replaceFirst("^www\\.", "");
            return protocol + "://" + host;
        } catch (MalformedURLException e) {
            log.error("Ошибка при нормализации URL: {}", url, e);
            return url;
        }
    }

    @Override
    public boolean isUrlAllowed(String url) {
        return siteRepository.findAll().stream()
                .anyMatch(site -> url.startsWith(site.getUrl())
                        || url.startsWith(site.getUrl().replace("://www.", "://"))
                        || url.startsWith(site.getUrl().replace("://", "://www.")));
    }
}