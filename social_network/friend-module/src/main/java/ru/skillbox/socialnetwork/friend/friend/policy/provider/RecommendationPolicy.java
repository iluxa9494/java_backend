package ru.skillbox.socialnetwork.friend.friend.policy.provider;

import ru.skillbox.socialnetwork.friend.friend.dto.friendship.StatusCode;

import java.util.Set;

public class RecommendationPolicy {

    public static Set<StatusCode> FRIEND_STATUSES_FOR_FINDING_FRIENDS_OF_FRIENDS_RECOMMENDATIONS = Set.of(StatusCode.NONE);

}
