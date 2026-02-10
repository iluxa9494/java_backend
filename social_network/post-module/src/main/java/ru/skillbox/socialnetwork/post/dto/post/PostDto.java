package ru.skillbox.socialnetwork.post.dto.post;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PostDto(
        UUID id,
        UUID authorId,
        String title,
        String text,               // из entity.postText
        long likeAmount,           // ← счётчик лайков
        long commentsCount,        // ← счётчик комментариев
        OffsetDateTime time,       // created
        OffsetDateTime timeChanged,// updated
        boolean deleted,
        boolean blocked
) {}