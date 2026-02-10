package ru.skillbox.socialnetwork.post.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.post.dto.reaction.ReactionDto;
import ru.skillbox.socialnetwork.post.dto.reaction.RequestReactionDto;
import ru.skillbox.socialnetwork.post.entity.EntityType;
import ru.skillbox.socialnetwork.post.entity.ReactionCount;
import ru.skillbox.socialnetwork.post.entity.ReactionType;
import ru.skillbox.socialnetwork.post.repository.ReactionCountRepository;
import ru.skillbox.socialnetwork.post.service.ReactionService;
import ru.skillbox.socialnetwork.post.util.CurrentUserProvider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ReactionController {

    private final ReactionService reactionService;
    private final CurrentUserProvider currentUserProvider;
    private final ReactionCountRepository reactionCountRepository;

    public ReactionController(ReactionService reactionService,
                              CurrentUserProvider currentUserProvider,
                              ReactionCountRepository reactionCountRepository) {
        this.reactionService = reactionService;
        this.currentUserProvider = currentUserProvider;
        this.reactionCountRepository = reactionCountRepository;
    }

    /* ========= POST ========= */

    @PostMapping("/post/{postId}/like")
    public ReactionDto reactToPost(@PathVariable UUID postId,
                                   @RequestBody(required = false) @Valid RequestReactionDto request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        ReactionType type = (request != null && request.reactionType() != null)
                ? ReactionType.valueOf(request.reactionType())
                : ReactionType.LIKE;

        var result = reactionService.react(userId, EntityType.POST, postId, type);
        return new ReactionDto(result.active(), result.quantity(), result.reaction().name());
    }

    @DeleteMapping("/post/{postId}/like")
    public ResponseEntity<Void> removeReactionFromPost(@PathVariable UUID postId) {
        UUID userId = currentUserProvider.getCurrentUserId();
        reactionService.remove(userId, EntityType.POST, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/post/{postId}/reactions")
    public Map<String, Long> getPostReactions(@PathVariable UUID postId) {
        return reactionCountRepository.findByEntityTypeAndEntityId(EntityType.POST, postId)
                .stream()
                .filter(rc -> rc.getCount() > 0) // ❌ убираем 0
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toMap(
                        rc -> rc.getReactionType().name(),
                        ReactionCount::getCount,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    /* ======== COMMENT ======== */

    @PostMapping("/post/{postId}/comment/{commentId}/like")
    public ReactionDto reactToComment(@PathVariable UUID postId,
                                      @PathVariable UUID commentId,
                                      @RequestBody(required = false) @Valid RequestReactionDto request) {
        UUID userId = currentUserProvider.getCurrentUserId();
        ReactionType type = (request != null && request.reactionType() != null)
                ? ReactionType.valueOf(request.reactionType())
                : ReactionType.LIKE;

        var result = reactionService.react(userId, EntityType.COMMENT, commentId, type);
        return new ReactionDto(result.active(), result.quantity(), result.reaction().name());
    }

    @DeleteMapping("/post/{postId}/comment/{commentId}/like")
    public ResponseEntity<Void> removeReactionFromComment(@PathVariable UUID postId,
                                                          @PathVariable UUID commentId) {
        UUID userId = currentUserProvider.getCurrentUserId();
        reactionService.remove(userId, EntityType.COMMENT, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/post/{postId}/comment/{commentId}/reactions")
    public Map<String, Long> getCommentReactions(@PathVariable UUID postId,
                                                 @PathVariable UUID commentId) {
        return reactionCountRepository.findByEntityTypeAndEntityId(EntityType.COMMENT, commentId)
                .stream()
                .filter(rc -> rc.getCount() > 0) // ❌ убираем 0
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toMap(
                        rc -> rc.getReactionType().name(),
                        ReactionCount::getCount,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    /* ======== AGGREGATE FOR ALL POSTS ======== */

    @GetMapping("/post/reactions")
    public Map<UUID, Map<ReactionType, Long>> getAllPostReactions() {
        return reactionCountRepository.findAll().stream()
                .filter(rc -> rc.getEntityType() == EntityType.POST && rc.getCount() > 0) // ❌ убираем 0
                .collect(Collectors.groupingBy(
                        ReactionCount::getEntityId,
                        Collectors.toMap(
                                ReactionCount::getReactionType,
                                ReactionCount::getCount
                        )
                ));
    }
}