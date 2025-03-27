package searchengine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.search.SearchRequest;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.indexing.IndexingService;
import searchengine.services.search.SearchService;
import searchengine.services.statistics.StatisticsService;

import java.net.MalformedURLException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final SearchService searchService;
    private final StatisticsService statisticsService;
    private final IndexingService indexingService;

    @Autowired
    public ApiController(StatisticsService statisticsService, IndexingService indexingService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @PostMapping("/startIndexing")
    public ResponseEntity<?> startIndexing() {
        boolean indexingStarted = indexingService.startIndexing();
        return indexingStarted
                ? ResponseEntity.ok(createSuccessResponse("Индексация запущена."))
                : ResponseEntity.badRequest().body(createErrorResponse("Индексация уже запущена."));
    }

    @PostMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing() {
        boolean indexingStopped = indexingService.stopIndexing();
        return indexingStopped
                ? ResponseEntity.ok(createSuccessResponse("Индексация остановлена."))
                : ResponseEntity.badRequest().body(createErrorResponse("Индексация не запущена."));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam(required = false) String url) {
        logger.info("Запрос на индексацию страницы: {}", url);

        if (url == null || url.trim().isEmpty()) {
            logger.warn("❌ URL пустой или отсутствует.");
            return ResponseEntity.badRequest().body(createErrorResponse("URL не может быть пустым."));
        }

        if (!isValidUrl(url)) {
            logger.warn("❌ Некорректный URL: {}", url);
            return ResponseEntity.badRequest().body(createErrorResponse("Некорректный формат URL."));
        }


        boolean siteExists = indexingService.siteExists(url);

        if (!siteExists && !url.endsWith("/")) {
            logger.info("Повторный поиск сайта с `/`...");
            siteExists = indexingService.siteExists(url + "/");
        }

        if (!siteExists) {
            logger.error("❌ Ошибка: сайт для URL {} не найден в БД!", url);
            return ResponseEntity.badRequest().body(createErrorResponse("Сайт не найден в базе данных."));
        }

        try {
            boolean pageIndexed = indexingService.indexPage(url);
            return pageIndexed
                    ? ResponseEntity.ok(createSuccessResponse("Страница успешно проиндексирована."))
                    : ResponseEntity.badRequest().body(createErrorResponse("Ошибка при индексации страницы."));
        } catch (Exception e) {
            logger.error("❌ Внутренняя ошибка сервера при индексации страницы: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Внутренняя ошибка сервера."));
        }
    }

    @PostMapping("/addSite")
    public ResponseEntity<?> addSite(@RequestParam String url, @RequestParam String name) {
        logger.info("Запрос на добавление сайта: {} ({})", name, url);

        boolean success = indexingService.addSite(url, name);
        return success
                ? ResponseEntity.ok(createSuccessResponse("Сайт успешно добавлен."))
                : ResponseEntity.badRequest().body(createErrorResponse("Сайт уже существует."));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        logger.info("Поиск по запросу: {} (Сайт: {})", request.getQuery(), request.getSite());

        if (request.getQuery() == null || request.getQuery().isEmpty()) {
            logger.warn("⚠Поисковый запрос не должен быть пустым.");
            return ResponseEntity.badRequest().body(createErrorResponse("Поисковый запрос не должен быть пустым."));
        }

        try {
            SearchResponse response = searchService.search(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("❌ Ошибка в поисковом запросе: {}", request.getQuery(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Ошибка обработки поиска."));
        }
    }


    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            new java.net.URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private Map<String, Object> createSuccessResponse(String message) {
        return Map.of("result", true, "message", message);
    }

    private Map<String, Object> createErrorResponse(String error) {
        return Map.of("result", false, "error", error);
    }
}