package ru.skillbox.socialnetwork.post.dto.filter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record PostFilter(
        Set<UUID> ids,
        Set<UUID> authorIds,
        String q,
        OffsetDateTime dateFrom,
        OffsetDateTime dateTo,
        Boolean isDeleted,
        Boolean isBlocked
) {}