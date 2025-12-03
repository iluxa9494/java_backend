package ru.skillbox.socialnetwork.friend.friend.outbox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.skillbox.socialnetwork.friend.friend.kafka.enums.EventType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "outbox_kafka_message")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboxMessage {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING) // сохраняет как строку в БД (например, "FRIEND_REQUEST_CREATED")
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String topic;

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Builder.Default
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "last_error")
    private String lastError;
}
