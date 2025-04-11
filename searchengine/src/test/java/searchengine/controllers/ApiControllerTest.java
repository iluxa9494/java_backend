package searchengine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import searchengine.config.SitesList;
import searchengine.dto.search.SuccessResponse;
import searchengine.dto.statistics.FinalStatisticsResponse;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.services.UrlValidationService;
import searchengine.services.indexing.IndexingService;
import searchengine.services.search.SearchService;
import searchengine.services.statistics.StatisticsService;
import java.util.Collections;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
public class ApiControllerTest {
    private final SuccessResponse successResponse = new SuccessResponse();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UrlValidationService urlValidationService;
    @MockBean
    private IndexingService indexingService;
    @MockBean
    private SitesList sitesList;
    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private SearchService searchService;

    @Test
    @DisplayName("GET /statistics should return statistics data")
    public void getStatisticsReturnStatistics() throws Exception {
        TotalStatistics totalStats = new TotalStatistics(0, 0, 0, true);
        StatisticsData data = new StatisticsData(totalStats, Collections.emptyList());
        FinalStatisticsResponse response = new FinalStatisticsResponse(true, data);
        when(statisticsService.getStatistics()).thenReturn(response);
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.statistics.total.sites").value(0));
    }

    @Test
    @DisplayName("GET /startIndexing should return success")
    public void startIndexingReturnSuccess() throws Exception {
        when(indexingService.startIndexing()).thenReturn(successResponse.isResult());
        mockMvc.perform(get("/api/startIndexing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @DisplayName("GET /stopIndexing should return success")
    public void stopIndexingReturnSuccess() throws Exception {
        when(indexingService.stopIndexing()).thenReturn(successResponse.isResult());
        mockMvc.perform(get("/api/stopIndexing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    @DisplayName("POST /indexPage with valid URL should return success")
    public void indexPageReturnSuccess() throws Exception {
        when(urlValidationService.isValidUrl("https://example.com")).thenReturn(true);
        when(indexingService.isUrlAllowed("https://example.com")).thenReturn(true);
        when(indexingService.indexPage("https://example.com")).thenReturn(true);
        mockMvc.perform(post("/api/indexPage")
                        .param("url", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }
}