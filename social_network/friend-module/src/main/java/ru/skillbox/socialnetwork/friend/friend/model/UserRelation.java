package ru.skillbox.socialnetwork.friend.friend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.StatusCode;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_relations")
public class UserRelation {

    @EmbeddedId
    private UserRelationId id;

    @Enumerated(EnumType.STRING)
    private StatusCode statusCode;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
