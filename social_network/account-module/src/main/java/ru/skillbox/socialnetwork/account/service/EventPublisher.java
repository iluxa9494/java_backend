package ru.skillbox.socialnetwork.account.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPasswordChangedEvent(Object event) {
        kafkaTemplate.send("user.password.changed", event);
    }

    public void sendEmailChangedEvent(Object event) {
        kafkaTemplate.send("user.email.changed", event);
    }
}
