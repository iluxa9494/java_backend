package com.team58.mc_notifications.service.impl;

import com.team58.mc_notifications.domain.Notification;
import com.team58.mc_notifications.dto.NotificationDto;
import com.team58.mc_notifications.dto.NotificationResponse;
import com.team58.mc_notifications.dto.CreateNotificationRequest;
import com.team58.mc_notifications.mapper.NotificationMapper;
import com.team58.mc_notifications.repository.NotificationRepository;
import com.team58.mc_notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    public NotificationResponse page(UUID userId, Pageable pageable) {
        return mapper.toResponse(repository.findByUserId(userId, pageable));
    }

    @Override
    public int count(UUID userId) {
        long v = repository.countByUserIdAndReadFalse(userId);
        return (v > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) v;
    }

    @Override
    public NotificationResponse markAllRead(UUID userId, Pageable pageable) {
        var page = repository.findByUserId(userId, pageable);
        page.forEach(n -> {
            if (!n.isRead()) n.setRead(true);
        });
        repository.saveAll(page.getContent());
        return mapper.toResponse(page);
    }

    @Override
    public NotificationDto create(CreateNotificationRequest rq) {
        if (rq.getUserId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        Notification n = new Notification();
        n.setId(UUID.randomUUID());
        n.setTime(OffsetDateTime.now(ZoneOffset.UTC));
        n.setAuthorId(rq.getAuthorId());
        n.setUserId(rq.getUserId());
        n.setContent(rq.getContent());
        n.setNotificationType(rq.getNotificationType());
        n.setStatusSent(false);
        n.setRead(false);

        n = repository.save(n);
        return mapper.toDto(n);
    }
}