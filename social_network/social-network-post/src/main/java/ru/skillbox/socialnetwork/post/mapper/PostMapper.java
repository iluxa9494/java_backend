package ru.skillbox.socialnetwork.post.mapper;

import ru.skillbox.socialnetwork.post.dto.post.PostDto;
import ru.skillbox.socialnetwork.post.entity.PostEntity;

public final class PostMapper {
    private PostMapper() {}

    public static PostDto toDto(PostEntity e) {
        return new PostDto(
                e.getId(),
                e.getAuthorId(),
                e.getTitle(),
                e.getPostText(),     // маппим в поле text
                e.getLikeAmount(),
                e.getCommentsCount(),
                e.getTime(),
                e.getTimeChanged(),
                e.isDeleted(),
                e.isBlocked()
        );
    }
}