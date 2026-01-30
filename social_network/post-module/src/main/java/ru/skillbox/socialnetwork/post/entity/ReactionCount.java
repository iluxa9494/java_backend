package ru.skillbox.socialnetwork.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Агрегированное количество реакций по типам.
 * entity_type — тоже PG enum reaction_entity_type.
 */
@Entity
@Table(
        name = "reaction_count",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_reaction_count_entity_type_id_type",
                columnNames = {"entity_type", "entity_id", "reaction_type"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReactionCount {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, columnDefinition = "reaction_entity_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 16)
    private ReactionType reactionType;

    @Column(name = "count", nullable = false)
    private long count;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}