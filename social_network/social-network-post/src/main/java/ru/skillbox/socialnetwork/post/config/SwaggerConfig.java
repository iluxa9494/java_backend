package ru.skillbox.socialnetwork.post.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI socialNetworkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Social Network Post API")
                        .description("API для постов, комментариев и реакций")
                        .version("1.0.0"));
    }
}