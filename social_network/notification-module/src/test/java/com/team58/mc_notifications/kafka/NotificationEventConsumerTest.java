package com.team58.mc_notifications.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team58.mc_notifications.dto.CreateNotificationRequest;
import com.team58.mc_notifications.kafka.consumer.NotificationEventConsumer;
import com.team58.mc_notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Test
    void onFriendRequest_shouldCreateNotification() {
        ObjectMapper objectMapper = new ObjectMapper();
        NotificationEventConsumer consumer =
                new NotificationEventConsumer(objectMapper, notificationService);

        UUID authorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String json = String.format(
                "{ \"authorId\": \"%s\", \"userId\": \"%s\", \"content\": \"hi\" }",
                authorId, userId
        );

        consumer.onFriendRequest(json);

        verify(notificationService).create(any(CreateNotificationRequest.class));
    }
}