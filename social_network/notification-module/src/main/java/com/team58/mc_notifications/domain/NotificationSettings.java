package com.team58.mc_notifications.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "notification_settings",
        indexes = {@Index(name = "idx_notification_settings_user", columnList = "user_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "friend_request", nullable = false)
    private boolean friendRequest = true;

    @Column(name = "friend_birthday", nullable = false)
    private boolean friendBirthday = true;

    @Column(name = "post_comment", nullable = false)
    private boolean postComment = true;

    @Column(name = "comment_comment", nullable = false)
    private boolean commentComment = true;

    @Column(name = "post", nullable = false)
    private boolean post = true;

    @Column(name = "message", nullable = false)
    private boolean message = true;

    @Column(name = "send_email_message", nullable = false)
    private boolean sendEmailMessage = false;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        if (updatedAt == null) updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}