package ru.skillbox.socialnetwork.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Реакции пользователей на посты/комментарии.
 * entity_type — PG enum reaction_entity_type.
 */
@Entity
@Table(
        name = "reaction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reaction_entity_user",
                        columnNames = {"entity_type", "entity_id", "user_id"}
                ),
                @UniqueConstraint(
                        name = "ux_reaction_user_entity_type",
                        columnNames = {"user_id", "entity_type", "entity_id", "reaction_type"}
                )
        }
)
@Getter @Setter @NoArgsConstructor
public class ReactionEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, columnDefinition = "reaction_entity_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 16)
    private ReactionType reactionType;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}