package ru.skillbox.socialnetwork.post.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Page of comments")
public record PageCommentDto(
        List<CommentDto> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}