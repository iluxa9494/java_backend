package com.team58.mc_notifications.mapper;

import com.team58.mc_notifications.domain.Notification;
import com.team58.mc_notifications.dto.DataWrapper;
import com.team58.mc_notifications.dto.NotificationDto;
import com.team58.mc_notifications.dto.NotificationResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NotificationMapper {

    NotificationDto toDto(Notification src);

    Notification toEntity(NotificationDto dto);

    /**
     * Маппинг страницы сущностей Notification в ответ для фронта.
     */
    default NotificationResponse toResponse(Page<Notification> page) {
        List<DataWrapper<NotificationDto>> content = page.getContent().stream()
                .map(this::toDto)
                .map(DataWrapper::new)
                .toList();

        return NotificationResponse.builder()
                .content(content)
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .build();
    }
}