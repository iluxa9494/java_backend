package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.search.ResultResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.search.SuccessResponse;
import searchengine.dto.statistics.FinalStatisticsResponse;
import searchengine.services.indexing.IndexingService;
import searchengine.services.search.SearchService;
import searchengine.services.statistics.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final SearchService searchService;
    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final HttpServletRequest request;

    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public FinalStatisticsResponse statistics() {
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    @ResponseStatus(HttpStatus.OK)
    public ResultResponse startIndexing() {
        indexingService.startIndexing();
        return new ResultResponse(true, null);
    }

    @GetMapping("/stopIndexing")
    @ResponseStatus(HttpStatus.OK)
    public ResultResponse stopIndexing() {
        indexingService.stopIndexing();
        return new ResultResponse(true, null);
    }

    @PostMapping("/indexPage")
    @ResponseStatus(HttpStatus.OK)
    public SuccessResponse indexPage(@RequestParam String url) {
        log.info("method=POST, contentType={}, contentLength={}, url={}",
                request.getContentType(), request.getContentLength(), url);
        indexingService.indexPage(url);
        return new SuccessResponse();
    }

    @PostMapping("/addSite")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> addSite(@RequestParam String url, @RequestParam String name) {
        logger.info("Запрос на добавление сайта: {} ({})", name, url);
        indexingService.addSite(url, name);
        return Map.of("result", true);
    }

    @GetMapping("/search")
    public SearchResponse search(
            @RequestParam String query,
            @RequestParam(required = false) String site,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return searchService.search(query.trim(), site, offset, limit);
    }
}