package com.team58.mc_notifications.mapper;

import com.team58.mc_notifications.domain.NotificationSettings;
import com.team58.mc_notifications.domain.NotificationType;
import com.team58.mc_notifications.dto.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface NotificationSettingsMapper {

    SettingsDto toDto(NotificationSettings e);

    NotificationSettings toEntity(SettingsDto d);

    default CreateSettingsResponse toCreateResponse(NotificationSettings e) {
        return CreateSettingsResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .friendRequest(e.isFriendRequest())
                .friendBirthday(e.isFriendBirthday())
                .postComment(e.isPostComment())
                .commentComment(e.isCommentComment())
                .post(e.isPost())
                .message(e.isMessage())
                .sendEmailMessage(e.isSendEmailMessage())
                .build();
    }

    default NotificationSettingsDto toAggregatedDto(NotificationSettings e) {
        List<SettingsResponse> items = new ArrayList<>(7);
        items.add(new SettingsResponse(e.isFriendRequest(), NotificationType.FRIEND_REQUEST));
        items.add(new SettingsResponse(e.isFriendBirthday(), NotificationType.FRIEND_BIRTHDAY));
        items.add(new SettingsResponse(e.isPostComment(), NotificationType.POST_COMMENT));
        items.add(new SettingsResponse(e.isCommentComment(), NotificationType.COMMENT_COMMENT));
        items.add(new SettingsResponse(e.isPost(), NotificationType.POST));
        items.add(new SettingsResponse(e.isMessage(), NotificationType.MESSAGE));
        items.add(new SettingsResponse(e.isSendEmailMessage(), NotificationType.SEND_EMAIL_MESSAGE));

        OffsetDateTime time = e.getUpdatedAt() != null ? e.getUpdatedAt() : OffsetDateTime.now(ZoneOffset.UTC);
        return NotificationSettingsDto.builder()
                .time(time)
                .data(items)
                .userId(e.getUserId())
                .build();
    }

    default void apply(@MappingTarget NotificationSettings e, SettingRequest rq) {
        boolean v = rq.isEnable();
        switch (rq.getNotificationType()) {
            case FRIEND_REQUEST -> e.setFriendRequest(v);
            case FRIEND_BIRTHDAY -> e.setFriendBirthday(v);
            case POST_COMMENT -> e.setPostComment(v);
            case COMMENT_COMMENT -> e.setCommentComment(v);
            case POST -> e.setPost(v);
            case MESSAGE -> e.setMessage(v);
            case SEND_EMAIL_MESSAGE -> e.setSendEmailMessage(v);
        }
        e.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
    }
}