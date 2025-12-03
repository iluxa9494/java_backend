package ru.skillbox.socialnetwork.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Value("${client.connect-timeout-seconds}")
    private int connectTimeout;

    @Value("${client.read-timeout-seconds}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.connectTimeout(Duration.ofSeconds(connectTimeout))
                .readTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }
}
