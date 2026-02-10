package ru.skillbox.socialnetwork.post.dto.reaction;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reaction response DTO")
public record ReactionDto(
        boolean active,
        long quantity,
        String reaction
) {}