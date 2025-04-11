package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.config.SitesList;
import searchengine.dto.search.ErrorResponse;
import searchengine.dto.search.SuccessResponse;
import searchengine.dto.statistics.FinalStatisticsResponse;
import searchengine.services.UrlValidationService;
import searchengine.services.indexing.IndexingService;
import searchengine.services.search.SearchService;
import searchengine.services.statistics.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Контроллер для обработки API-запросов, связанных с управлением индексацией,
 * поиском и получением статистики.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final SearchService searchService;
    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final HttpServletRequest request;
    private final UrlValidationService urlValidationService;

    @Autowired
    public ApiController(StatisticsService statisticsService, IndexingService indexingService, SearchService searchService,
                         SitesList sitesList, HttpServletRequest request, UrlValidationService urlValidationService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
        this.searchService = searchService;
        this.request = request;
        this.urlValidationService = urlValidationService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<FinalStatisticsResponse> statistics() {
        FinalStatisticsResponse statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing() {
        if (indexingService.startIndexing()) {
            return ResponseEntity.ok(Map.of("result", true));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(false, "Индексация уже запущена."));
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing() {
        if (indexingService.stopIndexing()) {
            return ResponseEntity.ok(Map.of("result", true));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(false, "Индексация не запущена."));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam String url) {
        log.info("method=POST, contentType={}, contentLength={}, url={}",
                request.getContentType(), request.getContentLength(), url);
        if (!urlValidationService.isValidUrl(url)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Некорректный формат URL"));
        }
        if (!indexingService.isUrlAllowed(url)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Страница находится за пределами индексируемых сайтов"));
        }
        indexingService.indexPage(url);
        return ResponseEntity.ok(new SuccessResponse());
    }

    @PostMapping("/addSite")
    public ResponseEntity<?> addSite(@RequestParam String url, @RequestParam String name) {
        logger.info("Запрос на добавление сайта: {} ({})", name, url);
        boolean success = indexingService.addSite(url, name);
        if (success) {
            return ResponseEntity.ok(Map.of("result", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(false, "Сайт уже существует."));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String query,
            @RequestParam(required = false) String site,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(false, "Задан пустой поисковый запрос")
            );
        }
        try {
            return searchService.search(query.trim(), site, offset, limit);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(false, e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse(false, "Ошибка при выполнении поиска")
            );
        }
    }
}