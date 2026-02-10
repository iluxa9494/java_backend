package ru.skillbox.socialnetwork.friend.recommendation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RecommendationFriendsDto {

    private UUID id;

    private UUID friendId;

    private String photo;

    private String firstName;

    private String lastName;

    public void setId(UUID id) {
        this.id = id;
        this.friendId = id;
    }
}
