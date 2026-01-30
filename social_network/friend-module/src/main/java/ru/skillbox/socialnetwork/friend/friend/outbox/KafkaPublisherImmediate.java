package ru.skillbox.socialnetwork.friend.friend.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.skillbox.socialnetwork.friend.friend.outbox.model.OutboxMessage;
import ru.skillbox.socialnetwork.friend.friend.outbox.repository.OutboxMessageRepository;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisherImmediate {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxMessageRepository outboxRepository;

    public void publishAfterCommit(OutboxMessage message) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    kafkaTemplate.send(message.getTopic(), message.getPayload()).get(5, TimeUnit.SECONDS);
                    message.setProcessedAt(OffsetDateTime.now());
                    outboxRepository.save(message);
                } catch (Exception e) {
                    log.error("Failed to send outbox message id={}", message.getId(), e);
                }
            }
        });
    }
}
