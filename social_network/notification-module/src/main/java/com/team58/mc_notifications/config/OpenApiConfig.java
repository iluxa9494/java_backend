package com.team58.mc_notifications.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@ConditionalOnProperty(name = "social.openapi.notification.enabled", havingValue = "true")
public class OpenApiConfig {

    @Bean
    public OpenAPI notificationOpenAPI() {
        final String schemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Notifications API")
                        .version("v1")
                        .description("""
                                Сервис уведомлений (ms-notification).
                                Для всех /api/v1/notifications/** требуется JWT-токен в заголовке:
                                Authorization: Bearer <token>
                                """)
                        .contact(new Contact().name("Team 58"))
                )
                .components(new Components()
                        .addSecuritySchemes(
                                schemeName,
                                new SecurityScheme()
                                        .name(HttpHeaders.AUTHORIZATION)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT токен, выданный mc-authentication")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }

    @Bean
    public GroupedOpenApi notificationsGroup() {
        return GroupedOpenApi.builder()
                .group("notifications")
                .pathsToMatch("/api/v1/notifications/**")
                .pathsToExclude("/actuator/**")
                .build();
    }
}
