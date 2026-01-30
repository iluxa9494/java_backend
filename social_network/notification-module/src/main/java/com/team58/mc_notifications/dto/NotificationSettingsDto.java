package com.team58.mc_notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingsDto {
    private OffsetDateTime time;
    private List<SettingsResponse> data;
    private UUID userId;
}