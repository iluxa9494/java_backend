package ru.skillbox.socialnetwork.post.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaSmokeListener {

    @KafkaListener(topics = "#{topicNames.post()}", groupId = "post-service-smoke")
    public void onPost(@Payload String value) {
        log.info("[KAFKA] post topic: {}", value);
    }

    @KafkaListener(topics = "#{topicNames.comment()}", groupId = "post-service-smoke")
    public void onComment(@Payload String value) {
        log.info("[KAFKA] comment topic: {}", value);
    }

    @KafkaListener(topics = "#{topicNames.reaction()}", groupId = "post-service-smoke")
    public void onReaction(@Payload String value) {
        log.info("[KAFKA] reaction topic: {}", value);
    }
}