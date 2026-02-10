package ru.skillbox.socialnetwork.post.entity;

/** Допустимые типы реакций. Используем только UPPER_CASE. */
public enum ReactionType {
    LIKE, DISLIKE, HEART, LAUGH, SAD, ANGRY, WOW, CARE, FIRE, CLAP, STAR, THINK;

    public static ReactionType fromNullable(String s) {
        if (s == null || s.isBlank()) return LIKE;
        return ReactionType.valueOf(s.trim().toUpperCase());
    }
}