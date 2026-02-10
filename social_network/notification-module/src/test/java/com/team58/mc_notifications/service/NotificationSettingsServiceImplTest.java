package com.team58.mc_notifications.service;

import com.team58.mc_notifications.domain.NotificationSettings;
import com.team58.mc_notifications.domain.NotificationType;
import com.team58.mc_notifications.dto.NotificationSettingsDto;
import com.team58.mc_notifications.dto.SettingRequest;
import com.team58.mc_notifications.dto.SettingsDto;
import com.team58.mc_notifications.dto.SettingsUpdateResponse;
import com.team58.mc_notifications.mapper.NotificationSettingsMapper;
import com.team58.mc_notifications.repository.NotificationSettingsRepository;
import com.team58.mc_notifications.service.impl.NotificationSettingsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificationSettingsServiceImplTest {

    @Mock
    private NotificationSettingsRepository repository;

    @Mock
    private NotificationSettingsMapper mapper;

    @InjectMocks
    private NotificationSettingsServiceImpl service;

    @Test
    void get_shouldReturnAggregatedDtoWhenSettingsExist() {
        UUID userId = UUID.randomUUID();
        NotificationSettings entity = new NotificationSettings();
        entity.setUserId(userId);
        NotificationSettingsDto dto = NotificationSettingsDto.builder()
                .userId(userId)
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(entity));
        when(mapper.toAggregatedDto(entity)).thenReturn(dto);

        NotificationSettingsDto result = service.get(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        verify(repository).findByUserId(userId);
        verify(mapper).toAggregatedDto(entity);
    }

    @Test
    void update_shouldSaveAndReturnMessageFlag() {
        UUID userId = UUID.randomUUID();
        NotificationSettings entity = new NotificationSettings();
        entity.setUserId(userId);
        entity.setMessage(true);

        SettingRequest rq = SettingRequest.builder()
                .enable(true)
                .notificationType(NotificationType.MESSAGE)
                .build();

        when(repository.findByUserId(userId)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        SettingsUpdateResponse response = service.update(userId, rq);

        verify(mapper).apply(entity, rq);
        verify(repository).save(entity);
        assertThat(response.isMessageEnabled()).isEqualTo(entity.isMessage());
        assertThat(response.getTime()).isNotNull();
    }

    @Test
    void create_shouldThrowConflictIfAlreadyExists() {
        UUID userId = UUID.randomUUID();
        SettingsDto dto = new SettingsDto();

        when(repository.findByUserId(userId))
                .thenReturn(Optional.of(new NotificationSettings()));

        assertThatThrownBy(() -> service.create(userId, dto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.CONFLICT);
    }
}