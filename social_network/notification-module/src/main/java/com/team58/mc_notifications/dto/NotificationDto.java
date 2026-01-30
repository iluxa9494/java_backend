package com.team58.mc_notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team58.mc_notifications.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private UUID id;

    /**
     * Время отправки уведомления.
     */
    @JsonProperty("sentTime")
    private OffsetDateTime time;

    private UUID authorId;
    private UUID userId;
    private String content;
    private NotificationType notificationType;

    /**
     * Статус доставки/отправки уведомления.
     */
    @JsonProperty("isStatusSent")
    private boolean statusSent;

    @JsonProperty("isRead")
    private boolean read;
}