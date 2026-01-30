package ru.skillbox.socialnetwork.authentication.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.authentication.events.ChangedEmailEvent;
import ru.skillbox.socialnetwork.authentication.events.ChangedPasswordEvent;
import ru.skillbox.socialnetwork.authentication.web.controllers.AuthController;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public abstract class KafkaEventListener {

    private final AuthController authController;

    @KafkaListener(topics = "${app.kafka.kafkaUserEmailChanged}",
            groupId = "${app.kafka.kafkaGroupId}",
            containerFactory = "kafkaChangedEmailEventConcurrentKafkaListenerContainerFactory")
    public void listenChangedEmailEvent(@Payload ChangedEmailEvent event,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        authController.changeEmail(event);
    }

    @KafkaListener(topics = "${app.kafka.kafkaUserPasswordChanged}",
            groupId = "${app.kafka.kafkaGroupId}",
            containerFactory = "kafkaChangedPasswordEventConcurrentKafkaListenerContainerFactory")
    public void listenChangedPasswordEvent(@Payload ChangedPasswordEvent event,
                                        @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                        @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        authController.changePassword(event);
    }
}