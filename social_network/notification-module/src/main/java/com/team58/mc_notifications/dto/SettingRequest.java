package com.team58.mc_notifications.dto;

import com.team58.mc_notifications.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingRequest {
    private boolean enable;
    private NotificationType notificationType;
}