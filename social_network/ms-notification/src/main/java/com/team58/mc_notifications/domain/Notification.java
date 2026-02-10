package com.team58.mc_notifications.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notifications_user_time", columnList = "user_id,time"),
                @Index(name = "idx_notifications_user_read", columnList = "user_id,is_read")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "time", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime time;

    @Column(name = "author_id", nullable = false, columnDefinition = "UUID")
    private UUID authorId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 32)
    private NotificationType notificationType;

    @Column(name = "is_status_sent", nullable = false)
    private boolean statusSent;

    @Column(name = "is_read", nullable = false)
    private boolean read;
}