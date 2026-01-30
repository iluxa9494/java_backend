package com.team58.mc_notifications.web;

import com.team58.mc_notifications.dto.*;
import com.team58.mc_notifications.security.SecurityUtils;
import com.team58.mc_notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    private Pageable fixSort(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "time")
        );
    }

    /**
     * GET /page — получить страницу уведомлений.
     */
    @GetMapping("/page")
    public ResponseEntity<NotificationResponse> page(@PageableDefault(size = 20) Pageable pageable) {
        UUID userId = securityUtils.getCurrentUserIdOrThrow();
        Pageable fixed = fixSort(pageable);

        NotificationResponse response = notificationService.page(userId, fixed);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /count — ответ в формате {"data": {"count": N}}
     */
    @GetMapping("/count")
    public ResponseEntity<DataWrapper<CountDto>> count() {
        UUID userId = securityUtils.getCurrentUserIdOrThrow();
        int c = notificationService.count(userId);
        CountDto countDto = new CountDto(c);
        return ResponseEntity.ok(new DataWrapper<>(countDto));
    }

    /**
     * PUT /readed — пометить все как прочитанные и вернуть страницу уведомлений.
     */
    @PutMapping("/readed")
    public ResponseEntity<NotificationResponse> markAllRead(@PageableDefault(size = 20) Pageable pageable) {
        UUID userId = securityUtils.getCurrentUserIdOrThrow();
        Pageable fixed = fixSort(pageable);

        NotificationResponse response = notificationService.markAllRead(userId, fixed);
        return ResponseEntity.ok(response);
    }

    /**
     * POST / — создать уведомление
     */
    @PostMapping
    public ResponseEntity<NotificationDto> create(@Valid @RequestBody CreateNotificationRequest rq) {
        NotificationDto dto = notificationService.create(rq);
        return ResponseEntity.status(201).body(dto);
    }
}