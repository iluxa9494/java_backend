package ru.skillbox.socialnetwork.post.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi postServiceApi() {
        return GroupedOpenApi.builder()
                .group("post-service")
                .packagesToScan("ru.skillbox.socialnetwork.post")
                .pathsToMatch("/api/**", "/_debug/**") // ✅ добавили debug
                .build();
    }
}