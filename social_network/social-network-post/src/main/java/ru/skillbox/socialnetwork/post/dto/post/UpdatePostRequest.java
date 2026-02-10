package ru.skillbox.socialnetwork.post.dto.post;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest(
        @NotBlank String title,
        @NotBlank String text
) {}