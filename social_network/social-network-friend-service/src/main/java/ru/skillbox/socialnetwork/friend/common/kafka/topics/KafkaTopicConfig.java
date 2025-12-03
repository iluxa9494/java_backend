package ru.skillbox.socialnetwork.friend.common.kafka.topics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("spring.kafka.topic")
@Data
@Component
public class KafkaTopicConfig {
    private String friendRequest = "friend-request";
}