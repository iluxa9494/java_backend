package ru.skillbox.socialnetwork.friend.friend.dto.friendship;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class FriendDto {

    private UUID friendId;

    private UUID id;

    private String firstName;

    private String lastName;

    private LocalDate birthDay;

    private String country;

    private String city;

    private String photo;

    private StatusCode statusCode;

    private Boolean isOnline;

    public void setFriendId(UUID friendId) {
        this.friendId = friendId;
        this.id = friendId;
    }
}
