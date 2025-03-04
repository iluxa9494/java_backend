//package searchengine.controllers;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class ApiControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    void testGetStatistics() throws Exception {
//        mockMvc.perform(get("/api/statistics"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(true));
//    }
//
//    @Test
//    void testStartIndexing() throws Exception {
//        mockMvc.perform(post("/api/startIndexing"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(true));
//    }
//
//    @Test
//    void testStartIndexingWhenAlreadyRunning() throws Exception {
//        mockMvc.perform(post("/api/startIndexing"))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(post("/api/startIndexing"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.error").value("Indexing is already in progress"));
//    }
//
//    @Test
//    void testStopIndexing() throws Exception {
//        mockMvc.perform(post("/api/startIndexing"))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(post("/api/stopIndexing"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(true));
//    }
//
//    @Test
//    void testStopIndexingWhenNotRunning() throws Exception {
//        mockMvc.perform(post("/api/stopIndexing"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.error").value("No indexing process is running"));
//    }
//
//    @Test
//    void testIndexPage() throws Exception {
//        mockMvc.perform(post("/api/indexPage")
//                        .param("url", "https://example.com"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(true));
//    }
//
//    @Test
//    void testIndexPageWithInvalidUrl() throws Exception {
//        mockMvc.perform(post("/api/indexPage")
//                        .param("url", "invalid-url"))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.result").value(false))
//                .andExpect(jsonPath("$.error").value("Invalid URL format"));
//    }
//}
