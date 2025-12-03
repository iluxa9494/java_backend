package com.team58.mc_notifications.service;

import com.team58.mc_notifications.domain.Notification;
import com.team58.mc_notifications.domain.NotificationType;
import com.team58.mc_notifications.dto.CreateNotificationRequest;
import com.team58.mc_notifications.dto.NotificationDto;
import com.team58.mc_notifications.dto.NotificationResponse;
import com.team58.mc_notifications.mapper.NotificationMapper;
import com.team58.mc_notifications.repository.NotificationRepository;
import com.team58.mc_notifications.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void page_shouldDelegateToRepositoryAndMapper() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);
        Notification entity = new Notification();
        Page<Notification> page = new PageImpl<>(List.of(entity));

        NotificationResponse mapped = NotificationResponse.builder()
                .content(List.of())
                .totalPages(1)
                .totalElements(1)
                .build();

        when(repository.findByUserId(userId, pageable)).thenReturn(page);
        when(mapper.toResponse(page)).thenReturn(mapped);

        NotificationResponse result = service.page(userId, pageable);

        assertThat(result).isSameAs(mapped);
        verify(repository).findByUserId(userId, pageable);
        verify(mapper).toResponse(page);
    }

    @Test
    void count_shouldClampToMaxInt() {
        UUID userId = UUID.randomUUID();

        when(repository.countByUserIdAndReadFalse(userId))
                .thenReturn((long) Integer.MAX_VALUE + 10L);

        int count = service.count(userId);

        assertThat(count).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void markAllRead_shouldSetUnreadToReadAndSave() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);

        Notification n1 = new Notification();
        n1.setRead(false);
        Notification n2 = new Notification();
        n2.setRead(true);

        Page<Notification> page = new PageImpl<>(List.of(n1, n2));
        NotificationResponse mapped = NotificationResponse.builder()
                .content(List.of())
                .totalPages(1)
                .totalElements(2)
                .build();

        when(repository.findByUserId(userId, pageable)).thenReturn(page);
        when(mapper.toResponse(page)).thenReturn(mapped);

        NotificationResponse result = service.markAllRead(userId, pageable);

        assertThat(n1.isRead()).isTrue();
        assertThat(n2.isRead()).isTrue();
        verify(repository).saveAll(page.getContent());
        assertThat(result).isSameAs(mapped);
    }

    @Test
    void create_shouldFillNotificationAndReturnDto() {
        UUID authorId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CreateNotificationRequest rq = CreateNotificationRequest.builder()
                .authorId(authorId)
                .userId(userId)
                .notificationType(NotificationType.POST)
                .content("hello")
                .build();

        Notification entity = new Notification();
        entity.setId(UUID.randomUUID());
        entity.setAuthorId(authorId);
        entity.setUserId(userId);
        entity.setContent("hello");
        entity.setNotificationType(NotificationType.POST);
        entity.setRead(false);
        entity.setStatusSent(false);
        entity.setTime(OffsetDateTime.now());

        NotificationDto dto = NotificationDto.builder()
                .id(entity.getId())
                .authorId(authorId)
                .userId(userId)
                .content("hello")
                .notificationType(NotificationType.POST)
                .build();

        when(repository.save(any(Notification.class))).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        NotificationDto result = service.create(rq);

        assertThat(result.getId()).isEqualTo(entity.getId());
        verify(repository).save(any(Notification.class));
        verify(mapper).toDto(entity);
    }
}