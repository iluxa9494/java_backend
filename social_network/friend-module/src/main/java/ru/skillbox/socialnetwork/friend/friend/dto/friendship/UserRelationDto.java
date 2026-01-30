package ru.skillbox.socialnetwork.friend.friend.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRelationDto {

    private UUID userId;

    private UUID friendId;

    private StatusCode statusCode;
}
