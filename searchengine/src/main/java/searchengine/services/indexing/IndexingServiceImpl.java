package searchengine.services.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.exceptions.general.UrlNotAllowedException;
import searchengine.exceptions.indexing.IndexingAlreadyRunningException;
import searchengine.exceptions.indexing.IndexingNotRunningException;
import searchengine.exceptions.indexing.SiteAlreadyExistsException;
import searchengine.exceptions.validation.InvalidUrlException;
import searchengine.model.*;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SearchIndexRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.UrlValidationService;
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
    private final SearchIndexRepository searchIndexRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final SiteStatusService siteStatusService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final DataCleaner dataCleaner;
    private final UrlValidationService urlValidationService;
    private volatile boolean stopFlag = false;

    @Override
    public void addSite(String url, String name) {
        if (siteRepository.findByUrl(url).isPresent()) {
            throw new SiteAlreadyExistsException("Сайт уже существует.");
        }
        Optional<String> availabilityError = getSiteAvailabilityError(url);
        if (availabilityError.isPresent()) {
            throw new UrlNotAllowedException("Сайт находится за пределами индексируемых сайтов");
        }
        Site site = new Site();
        site.setUrl(url);
        site.setName(name);
        site.setStatus(SiteStatus.INDEXED);
        site.setStatusTime(LocalDateTime.now());
        siteRepository.save(site);
        log.info("Сайт добавлен: {}", url);
    }

    @Override
    @Transactional
    public void startIndexing() {
        if (siteRepository.findByStatus(SiteStatus.INDEXING).size() > 0) {
            log.warn("Индексация уже запущена");
            throw new IndexingAlreadyRunningException("Индексация уже запущена");
        }
        stopFlag = false;
        List<Site> sitesToIndex = siteRepository.findAll().stream()
                .filter(site -> site.getStatus() == SiteStatus.INDEXED || site.getStatus() == SiteStatus.FAILED)
                .toList();
        if (sitesToIndex.isEmpty()) {
            log.warn("Нет сайтов со статусом INDEXED или FAILED для индексации");
            throw new IllegalStateException("Нет сайтов со статусом INDEXED или FAILED для индексации");
        }
        siteStatusService.markSitesAsIndexing(sitesToIndex);
        for (Site site : sitesToIndex) {
            executorService.submit(() -> indexSingleSite(site));
        }
        log.info("Отправлено {} задач(и) на индексацию в ExecutorService", sitesToIndex.size());
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
            indexPage(site.getUrl());
            if (stopFlag) {
                site.setStatus(SiteStatus.FAILED);
                site.setLastError("Индексация остановлена вручную");
                log.warn("Индексация прервана в момент парсинга сайта: {}", site.getUrl());
            } else {
                site.setStatus(SiteStatus.INDEXED);
                site.setLastError(null);
                log.info("Сайт успешно проиндексирован: {}", site.getUrl());
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
    public void stopIndexing() {
        List<Site> indexingSites = siteRepository.findByStatus(SiteStatus.INDEXING);
        if (indexingSites.isEmpty()) {
            log.warn("Индексация не запущена — нет сайтов со статусом INDEXING.");
            throw new IndexingNotRunningException("Индексация не запущена");
        }
        stopFlag = true;
        for (Site site : indexingSites) {
            site.setStatus(SiteStatus.FAILED);
            site.setLastError("Индексация остановлена вручную");
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
            log.info("Сайт принудительно переведён в FAILED: {}", site.getUrl());
        }
    }

    @Override
    public void indexPage(String url) {
        validateUrl(url);
        String siteUrl = normalizeUrl(url);
        String path;
        try {
            path = new URL(url).getPath();
            if (path.isEmpty()) path = "/";
            if (!path.startsWith("/")) path = "/" + path;
        } catch (MalformedURLException e) {
            log.error("Некорректный URL: {}", url, e);
            throw new InvalidUrlException("Некорректный формат URL");
        }
        Optional<Site> optSite = siteRepository.findByUrlIgnoreWww(siteUrl);
        if (optSite.isEmpty() && !siteUrl.contains("www.")) {
            String altUrl = siteUrl.replace("://", "://www.");
            log.debug("Попытка найти сайт по альтернативному URL: {}", altUrl);
            optSite = siteRepository.findByUrl(altUrl);
        }
        if (optSite.isEmpty()) {
            log.warn("Сайт не найден в базе данных: {}", siteUrl);
            throw new UrlNotAllowedException("Сайт находится за пределами индексируемых сайтов");
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
                searchIndexRepository.deleteAll(searchIndexRepository.findByPage(existing));
                pageRepository.delete(existing);
            }
            log.info("Создаём страницу для сайта: {}", site.getUrl());
            Page page = new Page();
            page.setSite(site);
            page.setPath(path);
            page.setCode(200);
            page.setTitle(title);
            page.setContent(content);
            page = pageRepository.save(page);
            log.info("Страница успешно сохранена: {}", page.getId());
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
                SearchIndex searchIndex = new SearchIndex();
                searchIndex.setPage(page);
                searchIndex.setLemma(lemma);
                searchIndex.setRank(freq);
                searchIndexRepository.save(searchIndex);
            }
            log.info("Страница проиндексирована: {}", url);
        } catch (IOException e) {
            log.error("Ошибка при загрузке страницы {}: {}", url, e.getMessage());
            throw new RuntimeException("Ошибка при индексации страницы: " + url, e);
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

    public void validateUrl(String url) {
        if (!urlValidationService.isValidUrl(url)) {
            throw new InvalidUrlException("Некорректный формат URL");
        }
    }
}