package ru.skillbox.socialnetwork.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.post.dto.comment.CommentDto;
import ru.skillbox.socialnetwork.post.entity.CommentEntity;
import ru.skillbox.socialnetwork.post.events.CommentEvent;
import ru.skillbox.socialnetwork.post.repository.CommentRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventPublisher eventPublisher;

    // ----------- CREATE -----------
    @Transactional
    public CommentDto create(UUID postId, UUID parentId, String text) {
        CommentEntity e = new CommentEntity();
        e.setId(UUID.randomUUID());
        e.setPostId(postId);
        e.setParentId(parentId);
        e.setCommentText(text);
        e.setBlocked(false);
        e.setDeleted(false);
        e.setLikeAmount(0L);
        e.setTime(OffsetDateTime.now());
        e.setTimeChanged(e.getTime());
        commentRepository.save(e);

        eventPublisher.publishComment(new CommentEvent(
                "CREATED",
                e.getId(), e.getPostId(), e.getParentId(),
                e.getCommentText(), e.getLikeAmount(), e.isDeleted(),
                e.getTime(), e.getTimeChanged()
        ));

        return toDto(e);
    }

    // ----------- UPDATE -----------
    @Transactional
    public CommentDto update(UUID commentId, String text) {
        CommentEntity e = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));
        e.setCommentText(text);
        e.setTimeChanged(OffsetDateTime.now());
        commentRepository.save(e);

        eventPublisher.publishComment(new CommentEvent(
                "UPDATED",
                e.getId(), e.getPostId(), e.getParentId(),
                e.getCommentText(), e.getLikeAmount(), e.isDeleted(),
                e.getTime(), e.getTimeChanged()
        ));

        return toDto(e);
    }

    // ----------- DELETE (soft) -----------
    @Transactional
    public void delete(UUID commentId) {
        CommentEntity e = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));
        e.setDeleted(true);
        e.setTimeChanged(OffsetDateTime.now());
        commentRepository.save(e);

        eventPublisher.publishComment(new CommentEvent(
                "DELETED",
                e.getId(), e.getPostId(), e.getParentId(),
                e.getCommentText(), e.getLikeAmount(), e.isDeleted(),
                e.getTime(), e.getTimeChanged()
        ));
    }

    // ----------- READ TOP-LEVEL -----------
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByPost(UUID postId, Pageable pageable) {
        return commentRepository.findAllByPostIdAndParentIdIsNull(postId, pageable)
                .map(this::toDto);
    }

    // ----------- READ REPLIES -----------
    @Transactional(readOnly = true)
    public Page<CommentDto> getSubComments(UUID parentId, Pageable pageable) {
        return commentRepository.findAllByParentId(parentId, pageable)
                .map(this::toDto);
    }

    // ----------- HELPER -----------
    private CommentDto toDto(CommentEntity e) {
        long subCount = commentRepository.countByParentId(e.getId());
        return new CommentDto(
                e.getId(),
                e.getCommentText(),
                e.getPostId(),
                e.getParentId(),
                e.isBlocked(),
                e.isDeleted(),
                e.getLikeAmount(),
                false,               // myLike заглушка
                (int) subCount,
                e.getTime(),
                e.getTimeChanged()
        );
    }
}