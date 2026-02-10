package ru.skillbox.socialnetwork.post.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Comment DTO")
public record CommentDto(
        UUID id,
        String commentText,
        UUID postId,
        UUID parentId,
        Boolean isBlocked,
        Boolean isDeleted,
        Long likeAmount,
        Boolean myLike,
        Integer commentsCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime time,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime timeChanged
) {}