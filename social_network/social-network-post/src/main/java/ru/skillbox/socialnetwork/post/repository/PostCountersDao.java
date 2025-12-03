package ru.skillbox.socialnetwork.post.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostCountersDao {

    private final JdbcTemplate jdbc;

    // ВНИМАНИЕ: таблица называется "post" (как в @Table(name = "post"))
    public int incrementPostLike(UUID postId) {
        return jdbc.update("UPDATE post SET like_amount = like_amount + 1 WHERE id = ?", postId);
    }

    public int decrementPostLike(UUID postId) {
        return jdbc.update("UPDATE post SET like_amount = GREATEST(like_amount - 1, 0) WHERE id = ?", postId);
    }

    public int addToCommentsCount(UUID postId, long delta) {
        return jdbc.update("UPDATE post SET comments_count = comments_count + ? WHERE id = ?", delta, postId);
    }

    public int subtractFromCommentsCount(UUID postId, long delta) {
        return jdbc.update("UPDATE post SET comments_count = GREATEST(comments_count - ?, 0) WHERE id = ?", delta, postId);
    }
}