package ru.skillbox.socialnetwork.friend.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("kafka-publisher-emergency")
@Data
@Component
public class KafkaPublisherEmergencyConfig {
    private int maxAttempts = 5;
    private int fixedDelay = 180000; // ms
    private int batchSize = 20;
    private int timeout = 5; // seconds
}
