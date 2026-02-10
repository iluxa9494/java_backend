package com.team58.mc_notifications.web;

import com.team58.mc_notifications.dto.CountDto;
import com.team58.mc_notifications.dto.DataWrapper;
import com.team58.mc_notifications.dto.NotificationResponse;
import com.team58.mc_notifications.security.SecurityUtils;
import com.team58.mc_notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private NotificationController controller;

    @Test
    void page_shouldUseCurrentUserAndReturnResponse() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);

        when(securityUtils.getCurrentUserIdOrThrow()).thenReturn(userId);

        NotificationResponse response = NotificationResponse.builder()
                .content(List.of())
                .totalPages(1)
                .totalElements(0)
                .build();

        when(notificationService.page(eq(userId), any(Pageable.class)))
                .thenReturn(response);

        ResponseEntity<NotificationResponse> result = controller.page(pageable);

        assertThat(result.getBody()).isSameAs(response);
        verify(securityUtils).getCurrentUserIdOrThrow();
        verify(notificationService).page(eq(userId), any(Pageable.class));
    }

    @Test
    void count_shouldWrapResultIntoDataWrapper() {
        UUID userId = UUID.randomUUID();
        when(securityUtils.getCurrentUserIdOrThrow()).thenReturn(userId);
        when(notificationService.count(userId)).thenReturn(5);

        ResponseEntity<DataWrapper<CountDto>> result = controller.count();

        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getData().getCount()).isEqualTo(5);
    }
}