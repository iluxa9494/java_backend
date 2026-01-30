package com.team58.mc_notifications.web;

import com.team58.mc_notifications.dto.*;
import com.team58.mc_notifications.security.SecurityUtils;
import com.team58.mc_notifications.service.NotificationSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications/settings")
public class NotificationSettingsController {

    private final NotificationSettingsService settingsService;
    private final SecurityUtils securityUtils;

    /**
     * GET - текущие настройки пользователя
     */
    @GetMapping
    public ResponseEntity<NotificationSettingsDto> get() {
        UUID userId = securityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(settingsService.get(userId));
    }

    /**
     * PUT - обновить одну настройку (enable + notificationType)
     */
    @PutMapping
    public ResponseEntity<SettingsUpdateResponse> update(@Valid @RequestBody SettingRequest rq) {
        UUID userId = securityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(settingsService.update(userId, rq));
    }

    /**
     * POST - создать полный набор настроек (если ещё не создан)
     */
    @PostMapping
    public ResponseEntity<CreateSettingsResponse> create(@Valid @RequestBody SettingsDto dto) {
        UUID userId = securityUtils.getCurrentUserIdOrThrow();
        return ResponseEntity.ok(settingsService.create(userId, dto));
    }
}