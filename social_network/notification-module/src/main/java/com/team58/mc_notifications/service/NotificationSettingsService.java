package com.team58.mc_notifications.service;

import com.team58.mc_notifications.dto.*;

import java.util.UUID;

public interface NotificationSettingsService {

    NotificationSettingsDto get(UUID userId);

    SettingsUpdateResponse update(UUID userId, SettingRequest rq);

    CreateSettingsResponse create(UUID userId, SettingsDto dto);
}