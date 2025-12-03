package ru.skillbox.socialnetwork.post.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @Column(name = "time_changed", nullable = false)
    private OffsetDateTime timeChanged;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "post_text", nullable = false, columnDefinition = "TEXT")
    private String postText;

    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "comments_count", nullable = false)
    private long commentsCount = 0L;

    @Column(name = "tags")
    private String tags;

    @Column(name = "like_amount", nullable = false)
    private long likeAmount = 0L;

    // getters / setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OffsetDateTime getTime() { return time; }
    public void setTime(OffsetDateTime time) { this.time = time; }

    public OffsetDateTime getTimeChanged() { return timeChanged; }
    public void setTimeChanged(OffsetDateTime timeChanged) { this.timeChanged = timeChanged; }

    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPostText() { return postText; }
    public void setPostText(String postText) { this.postText = postText; }

    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public long getLikeAmount() { return likeAmount; }
    public void setLikeAmount(long likeAmount) { this.likeAmount = likeAmount; }
}