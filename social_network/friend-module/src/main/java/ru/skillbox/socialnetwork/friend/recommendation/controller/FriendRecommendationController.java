package ru.skillbox.socialnetwork.friend.recommendation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.friend.common.security.SecurityUtils;
import ru.skillbox.socialnetwork.friend.recommendation.dto.RecommendationFriendsDto;
import ru.skillbox.socialnetwork.friend.recommendation.service.RecommendationService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendRecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<RecommendationFriendsDto> findPageRecommendationFriends(Pageable pageable) {
        Page<RecommendationFriendsDto> page = recommendationService.recommendedFriends(SecurityUtils.getCurrentUserId(), pageable);
        log.info("Возвращается список рекомендумеых друзей размером : {}", page.getContent().size());

        return page.getContent();
    }
}
