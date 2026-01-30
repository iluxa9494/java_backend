package ru.skillbox.socialnetwork.post.service;

import ru.skillbox.socialnetwork.post.entity.ReactionType;

public record ReactionResult(
        boolean active,
        long quantity,
        ReactionType reaction
) { }