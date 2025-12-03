package ru.skillbox.socialnetwork.friend.friend.dto.account;

import java.util.UUID;

public record AccountStatus(
         UUID id,
         boolean isDeleted,
         boolean isBlocked,
         boolean isNotFound
) {}
