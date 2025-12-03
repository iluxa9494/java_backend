package com.team58.mc_notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Обёртка ответа для /api/v1/notifications/page.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    /**
     * Список уведомлений, обёрнутых в DataWrapper.
     */
    private List<DataWrapper<NotificationDto>> content;

    private int totalPages;
    private int totalElements;
}