package com.team58.mc_notifications.kafka.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Базовый класс для всех notification-событий.
 */
@Data
@NoArgsConstructor
public abstract class BaseNotificationEvent implements NotificationEvent {

    private UUID eventId;
    private OffsetDateTime time;
    private UUID authorId;
    private UUID userId;
    private String content;
}