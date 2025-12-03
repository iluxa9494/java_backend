package ru.skillbox.socialnetwork.friend.recommendation.service;

import java.util.UUID;

public interface AffinityService {

    void increaseAffinity(UUID userId, UUID targetId, Double weight);

}
