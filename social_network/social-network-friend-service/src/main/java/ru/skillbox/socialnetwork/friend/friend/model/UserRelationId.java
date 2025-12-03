package ru.skillbox.socialnetwork.friend.friend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRelationId implements Serializable {

    @Column(name = "id_user_id")
    private UUID userId;

    @Column(name = "id_friend_id")
    private UUID friendId;

}
