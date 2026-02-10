package ru.skillbox.socialnetwork.friend.friend.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.friend.friend.kafka.enums.EventType;
import ru.skillbox.socialnetwork.friend.friend.kafka.message.NotificationEvent;
import ru.skillbox.socialnetwork.friend.friend.outbox.KafkaPublisherImmediate;
import ru.skillbox.socialnetwork.friend.friend.outbox.model.OutboxMessage;
import ru.skillbox.socialnetwork.friend.friend.outbox.repository.OutboxMessageRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaEventService {

    private final KafkaTopicService kafkaTopicService;
    private final OutboxMessageRepository outboxRepository;
    private final KafkaPublisherImmediate kafkaPublisherImmediate;
    private final ObjectMapper objectMapper;

    @Transactional(transactionManager = "friendTransactionManager")
    public void publishEvent(UUID senderId, UUID recipientId, EventType eventType) {

        NotificationEvent event = new NotificationEvent();
        event.setEventId(UUID.randomUUID());
        event.setTime(OffsetDateTime.now());
        event.setAuthorId(senderId);
        event.setUserId(recipientId);
        event.setContent(kafkaTopicService.getContentFor(eventType));

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Не удалось сериализовать событие", e);
        }

        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setId(UUID.randomUUID());
        outboxMessage.setTopic(kafkaTopicService.getTopicFor(eventType));
        outboxMessage.setPayload(payloadJson);
        outboxMessage.setEventType(eventType);
        outboxMessage.setCreatedAt(event.getTime());

        outboxRepository.save(outboxMessage);
        kafkaPublisherImmediate.publishAfterCommit(outboxMessage);
    }
}
