package ru.skillbox.socialnetwork.post.dto.reaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Reaction request DTO")
public record RequestReactionDto(
        @Schema(
                description = "Reaction type",
                example = "LIKE",
                allowableValues = {
                        "LIKE", "DISLIKE", "HEART", "LAUGH", "SAD", "ANGRY",
                        "WOW", "CARE", "FIRE", "CLAP", "STAR", "THINK"
                }
        )
        @Pattern(
                regexp = "LIKE|DISLIKE|HEART|LAUGH|SAD|ANGRY|WOW|CARE|FIRE|CLAP|STAR|THINK",
                message = "reactionType must be one of: LIKE, DISLIKE, HEART, LAUGH, SAD, ANGRY, WOW, CARE, FIRE, CLAP, STAR, THINK"
        )
        String reactionType
) {}