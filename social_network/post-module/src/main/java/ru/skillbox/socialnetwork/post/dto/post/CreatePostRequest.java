package ru.skillbox.socialnetwork.post.dto.post;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest(
        @NotBlank String title,
        @NotBlank String text
) {}