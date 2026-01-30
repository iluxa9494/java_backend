package com.team58.mc_notifications.dto;

import com.team58.mc_notifications.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {
    private UUID authorId;
    private UUID userId;
    private NotificationType notificationType;
    private String content;
}