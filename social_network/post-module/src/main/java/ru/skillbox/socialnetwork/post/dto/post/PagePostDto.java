package ru.skillbox.socialnetwork.post.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Page of posts")
public record PagePostDto(
        @Schema(description = "Page content")
        List<PostDto> content,

        @Schema(description = "Current page number (0-based)", example = "0")
        int pageNumber,

        @Schema(description = "Page size", example = "10")
        int pageSize,

        @Schema(description = "Total elements", example = "0")
        long totalElements,

        @Schema(description = "Total pages", example = "0")
        int totalPages
) {}