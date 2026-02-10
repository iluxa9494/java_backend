package com.team58.mc_notifications.web;

import com.team58.mc_notifications.dto.NotificationSettingsDto;
import com.team58.mc_notifications.dto.SettingRequest;
import com.team58.mc_notifications.dto.SettingsUpdateResponse;
import com.team58.mc_notifications.security.SecurityUtils;
import com.team58.mc_notifications.service.NotificationSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificationSettingsControllerTest {

    @Mock
    private NotificationSettingsService settingsService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private NotificationSettingsController controller;

    @Test
    void get_shouldReturnSettingsForCurrentUser() {
        UUID userId = UUID.randomUUID();
        NotificationSettingsDto dto = NotificationSettingsDto.builder()
                .userId(userId)
                .build();

        when(securityUtils.getCurrentUserIdOrThrow()).thenReturn(userId);
        when(settingsService.get(userId)).thenReturn(dto);

        ResponseEntity<NotificationSettingsDto> result = controller.get();

        assertThat(result.getBody()).isSameAs(dto);
        verify(settingsService).get(userId);
    }

    @Test
    void update_shouldReturnSettingsUpdateResponse() {
        UUID userId = UUID.randomUUID();
        SettingRequest rq = SettingRequest.builder().build();
        SettingsUpdateResponse rs = SettingsUpdateResponse.builder().messageEnabled(true).build();

        when(securityUtils.getCurrentUserIdOrThrow()).thenReturn(userId);
        when(settingsService.update(userId, rq)).thenReturn(rs);

        ResponseEntity<SettingsUpdateResponse> result = controller.update(rq);

        assertThat(result.getBody()).isSameAs(rs);
        verify(settingsService).update(userId, rq);
    }
}