package ru.skillbox.socialnetwork.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.post.repository.CommentCountersDao;
import ru.skillbox.socialnetwork.post.repository.PostCountersDao;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final PostCountersDao postCountersDao;
    private final CommentCountersDao commentCountersDao;

    /* -------- ЛАЙКИ ПОСТА -------- */
    @Transactional
    public void onPostLikeAdded(UUID postId) {
        postCountersDao.incrementPostLike(postId);
    }

    @Transactional
    public void onPostLikeRemoved(UUID postId) {
        postCountersDao.decrementPostLike(postId);
    }

    /* -------- ЛАЙКИ КОММЕНТАРИЯ (если считаем в comment.like_amount) -------- */
    @Transactional
    public void onCommentLikeAdded(UUID commentId) {
        commentCountersDao.incrementCommentLike(commentId);
    }

    @Transactional
    public void onCommentLikeRemoved(UUID commentId) {
        commentCountersDao.decrementCommentLike(commentId);
    }

    /* -------- СЧЁТЧИК КОММЕНТАРИЕВ У ПОСТА -------- */
    @Transactional
    public void onCommentCreated(UUID postId) {
        postCountersDao.addToCommentsCount(postId, 1);
    }

    @Transactional
    public void onCommentDeleted(UUID postId, long delta) {
        long d = delta < 1 ? 1 : delta;
        postCountersDao.subtractFromCommentsCount(postId, d);
    }
}