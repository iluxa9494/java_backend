package ru.skillbox.socialnetwork.friend.friend.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.friend.common.configuration.KafkaPublisherEmergencyConfig;
import ru.skillbox.socialnetwork.friend.friend.outbox.model.OutboxMessage;
import ru.skillbox.socialnetwork.friend.friend.outbox.repository.OutboxMessageRepository;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisherEmergency {

    private final KafkaPublisherEmergencyConfig config;
    private final OutboxMessageRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${kafka-publisher-emergency.fixed-delay:180000}")
    public void publishUnsentMessages() {

        for (int i = 0; i < config.getBatchSize(); i++) {
            if (!processNextMessage()) {
                break;
            };
        }
    }

    @Transactional
    private boolean processNextMessage() {
        OutboxMessage message;
        try {
            message = outboxRepository.findNextUnprocessedMessage(config.getMaxAttempts());
        } catch (Exception e) {
            log.error("Failed to fetch next outbox message", e);
            return false;
        }

        if (message == null) return false;

        boolean sentToKafka = false;
        String lastError = null;

        try {
            kafkaTemplate.send(message.getTopic(), message.getPayload()).get(config.getTimeout(), TimeUnit.SECONDS);
            sentToKafka = true;
        } catch (Exception e) {
            lastError = trimErrorMessage(e.getMessage());
            log.warn("Kafka send failed for message id={}. Error: {}", message.getId(), lastError);
        }

        try {
            if (sentToKafka) {
                message.setProcessedAt(OffsetDateTime.now());
                message.setLastError(null);
                log.debug("Marked message id={} as processed", message.getId());
            } else {
                message.setLastError(lastError);
                message.setAttemptCount(message.getAttemptCount() + 1);
            }
            outboxRepository.save(message);
        } catch (Exception e) {
            log.error("Failed to update outbox message id={} in DB. Kafka sent: {}. DB error: {}",
                    message.getId(), sentToKafka, e.getMessage(), e);
        }

        return true;
    }

    private String trimErrorMessage(String msg) {
        if (msg == null || msg.length() <= 999) return msg;
        return msg.substring(0, 996) + "...";
    }
}