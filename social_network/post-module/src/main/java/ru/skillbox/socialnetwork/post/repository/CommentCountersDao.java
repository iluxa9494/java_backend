package ru.skillbox.socialnetwork.post.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentCountersDao {

    private final JdbcTemplate jdbc;

    // Таблица "comment" (как в @Table(name = "comment"))
    public Optional<UUID> findPostIdByCommentId(UUID commentId) {
        try {
            UUID id = jdbc.queryForObject(
                    "SELECT post_id FROM comment WHERE id = ?",
                    (rs, rn) -> (UUID) rs.getObject(1),
                    commentId
            );
            return Optional.ofNullable(id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int incrementCommentLike(UUID commentId) {
        return jdbc.update("UPDATE comment SET like_amount = like_amount + 1 WHERE id = ?", commentId);
    }

    public int decrementCommentLike(UUID commentId) {
        return jdbc.update("UPDATE comment SET like_amount = GREATEST(like_amount - 1, 0) WHERE id = ?", commentId);
    }

    public long countVisibleCommentsByPost(UUID postId) {
        Long cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM comment WHERE post_id = ? AND is_deleted = false",
                Long.class,
                postId
        );
        return cnt == null ? 0L : cnt;
    }
}