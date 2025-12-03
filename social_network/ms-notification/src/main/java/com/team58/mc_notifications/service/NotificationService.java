package com.team58.mc_notifications.service;

import com.team58.mc_notifications.dto.NotificationDto;
import com.team58.mc_notifications.dto.NotificationResponse;
import com.team58.mc_notifications.dto.CreateNotificationRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    /**
     * Постраничный список уведомлений пользователя.
     */
    NotificationResponse page(UUID userId, Pageable pageable);

    /**
     * Количество непрочитанных уведомлений.
     */
    int count(UUID userId);

    /**
     * Пометить все уведомления пользователя как прочитанные
     * и вернуть актуальную страницу.
     */
    NotificationResponse markAllRead(UUID userId, Pageable pageable);

    /**
     * Создание уведомления (Kafka / внутренние REST-сценарии).
     */
    NotificationDto create(CreateNotificationRequest rq);
}