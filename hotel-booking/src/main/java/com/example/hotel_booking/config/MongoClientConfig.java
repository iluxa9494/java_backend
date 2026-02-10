package com.example.hotel_booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoClientConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(
            @Value("${app.mongodb.connect-timeout:2s}") Duration connectTimeout,
            @Value("${app.mongodb.socket-timeout:5s}") Duration socketTimeout,
            @Value("${app.mongodb.server-selection-timeout:3s}") Duration serverSelectionTimeout,
            @Value("${app.mongodb.max-pool-size:20}") int maxPoolSize,
            @Value("${app.mongodb.min-pool-size:2}") int minPoolSize,
            @Value("${app.mongodb.max-idle-time:60s}") Duration maxIdleTime,
            @Value("${app.mongodb.max-wait-time:5s}") Duration maxWaitTime) {
        return builder -> {
            builder.applyToSocketSettings(settings -> {
                settings.connectTimeout((int) connectTimeout.toMillis(), TimeUnit.MILLISECONDS);
                settings.readTimeout((int) socketTimeout.toMillis(), TimeUnit.MILLISECONDS);
            });
            builder.applyToClusterSettings(settings ->
                    settings.serverSelectionTimeout(serverSelectionTimeout.toMillis(), TimeUnit.MILLISECONDS));
            builder.applyToConnectionPoolSettings(settings -> {
                settings.maxSize(maxPoolSize);
                settings.minSize(minPoolSize);
                settings.maxConnectionIdleTime(maxIdleTime.toMillis(), TimeUnit.MILLISECONDS);
                settings.maxWaitTime(maxWaitTime.toMillis(), TimeUnit.MILLISECONDS);
            });
        };
    }
}
