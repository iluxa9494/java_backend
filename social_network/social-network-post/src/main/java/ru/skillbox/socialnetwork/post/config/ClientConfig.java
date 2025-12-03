package ru.skillbox.socialnetwork.post.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Bean("externalRestTemplate")
    public RestTemplate externalRestTemplate(RestTemplateBuilder builder) {
        return builder.connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Bean("loadBalancedRestTemplate")
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(20))
                .build();
    }
}
