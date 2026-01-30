package com.team58.mc_notifications.service.impl;

import com.team58.mc_notifications.domain.NotificationSettings;
import com.team58.mc_notifications.dto.*;
import com.team58.mc_notifications.mapper.NotificationSettingsMapper;
import com.team58.mc_notifications.repository.NotificationSettingsRepository;
import com.team58.mc_notifications.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "notificationTransactionManager")
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private final NotificationSettingsRepository repository;
    private final NotificationSettingsMapper mapper;

    @Override
    public NotificationSettingsDto get(UUID userId) {
        NotificationSettings e = repository.findByUserId(userId).orElseGet(() -> {
            NotificationSettings created = newDefaults(userId);
            try {
                return repository.save(created);
            } catch (DataAccessException ex) {
                log.error("Failed to create default settings for user {}", userId, ex);
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to init default settings"
                );
            }
        });
        return mapper.toAggregatedDto(e);
    }

    @Override
    public SettingsUpdateResponse update(UUID userId, SettingRequest rq) {
        NotificationSettings e = repository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Settings not found"
                ));

        mapper.apply(e, rq);

        try {
            repository.save(e);
        } catch (DataAccessException ex) {
            log.error("Failed to update settings for user {}", userId, ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DB update failed"
            );
        }

        boolean messageEnabled = e.isMessage();

        return SettingsUpdateResponse.builder()
                .time(OffsetDateTime.now(ZoneOffset.UTC))
                .messageEnabled(messageEnabled)
                .build();
    }

    @Override
    public CreateSettingsResponse create(UUID userId, SettingsDto dto) {
        if (repository.findByUserId(userId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Settings already exist for this user"
            );
        }

        NotificationSettings e = mapper.toEntity(dto);
        e.setId(UUID.randomUUID());
        e.setUserId(userId);
        e.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        try {
            e = repository.save(e);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate settings insert for user {}", userId, ex);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Settings already exist for this user"
            );
        } catch (DataAccessException ex) {
            log.error("Failed to create settings for user {}", userId, ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "DB insert failed"
            );
        }

        return mapper.toCreateResponse(e);
    }

    private NotificationSettings newDefaults(UUID userId) {
        NotificationSettings e = new NotificationSettings();
        e.setId(UUID.randomUUID());
        e.setUserId(userId);
        e.setFriendRequest(true);
        e.setFriendBirthday(true);
        e.setPostComment(true);
        e.setCommentComment(true);
        e.setPost(true);
        e.setMessage(true);
        e.setSendEmailMessage(false);
        e.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return e;
    }
}
