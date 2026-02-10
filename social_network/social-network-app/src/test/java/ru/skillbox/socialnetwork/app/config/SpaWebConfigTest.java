package ru.skillbox.socialnetwork.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(properties = "server.servlet.context-path=/projects/social-network")
@Import(SpaWebConfig.class)
class SpaWebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void spaRouteWithoutDotForwardsToIndex() throws Exception {
        mockMvc.perform(get("/projects/social-network/some/page"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("SPA_INDEX_TEST")))
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void staticAssetWithDotIsNotForwardedToIndex() throws Exception {
        mockMvc.perform(get("/projects/social-network/assets/app.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("console.log('asset');")))
                .andExpect(forwardedUrl(null));
    }

    @Test
    void apiPathIsNotHandledBySpaFallback() throws Exception {
        mockMvc.perform(get("/projects/social-network/api/actuator/health"))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl(null));
    }
}
