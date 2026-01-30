package ru.skillbox.socialnetwork.friend.friend.dto.friendship;

public record FriendshipPairDto(
        UserRelationDto userToFriend,
        UserRelationDto friendToUser
) {}
