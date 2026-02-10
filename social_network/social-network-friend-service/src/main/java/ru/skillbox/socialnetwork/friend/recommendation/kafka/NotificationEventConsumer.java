//package ru.skillbox.socialnetwork.friend.recommendation.kafka;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import ru.skillbox.socialnetwork.friend.recommendation.kafka.events.LikeEvent;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class NotificationEventConsumer {
//
//    private final ObjectMapper objectMapper;
//    private final NotificationService notificationService;
//
//    // ---------- LIKE ----------
//    @KafkaListener(
//            topics = "${kafka.topic.like:like}",
//            groupId = "${spring.kafka.consumer.group-id:ms-notification}"
//    )
//    public void onLike(String payload) {
//        LikeEvent e = parse(payload, LikeEvent.class);
//        if (e == null) return;
//
//    }
//
//    // ---------- COMMENT ----------
//    @KafkaListener(
//            topics = "${kafka.topic.comment:comment}",
//            groupId = "${spring.kafka.consumer.group-id:ms-notification}"
//    )
//    public void onComment(String payload) {
//        CommentEvent e = parse(payload, CommentEvent.class);
//        if (e == null) return;
//        create(e.getAuthorId(), e.getUserId(),
//                resolveType("POST_COMMENT", NotificationType.MESSAGE),
//                defaultContent(e.getContent(), "Новый комментарий к вашему посту"));
//    }
//
//    // ---------- REPLY ----------
//    @KafkaListener(
//            topics = "${kafka.topic.reply:reply}",
//            groupId = "${spring.kafka.consumer.group-id:ms-notification}"
//    )
//    public void onReply(String payload) {
//        ReplyEvent e = parse(payload, ReplyEvent.class);
//        if (e == null) return;
//        create(e.getAuthorId(), e.getUserId(),
//                resolveType("COMMENT_COMMENT", NotificationType.MESSAGE),
//                defaultContent(e.getContent(), "Ответ на ваш комментарий"));
//    }
//
//    // ---------- POST ----------
//    @KafkaListener(
//            topics = "${kafka.topic.post:post}",
//            groupId = "${spring.kafka.consumer.group-id:ms-notification}"
//    )
//    public void onPost(String payload) {
//        PostEvent e = parse(payload, PostEvent.class);
//        if (e == null) return;
//        create(e.getAuthorId(), e.getUserId(),
//                resolveType("POST", NotificationType.MESSAGE),
//                defaultContent(e.getContent(), "У друга новый пост"));
//    }
//
//    // ---------- BIRTHDAY ----------
//    @KafkaListener(
//            topics = "${kafka.topic.birthday:birthday}",
//            groupId = "${spring.kafka.consumer.group-id:ms-notification}"
//    )
//    public void onBirthday(String payload) {
//        BirthdayEvent e = parse(payload, BirthdayEvent.class);
//        if (e == null) return;
//        create(e.getAuthorId(), e.getUserId(),
//                resolveType("FRIEND_BIRTHDAY", NotificationType.MESSAGE),
//                defaultContent(e.getContent(), "У друга день рождения"));
//    }
//
//    // ---------- MESSAGE ----------
//    @KafkaListener(
//            topics = "${kafka.topic.message:message}",
//            groupId = "${spring.kafka.consumer.group-id:ms-notification}"
//    )
//    public void onMessage(String payload) {
//        MessageEvent e = parse(payload, MessageEvent.class);
//        if (e == null) return;
//        create(e.getAuthorId(), e.getUserId(),
//                resolveType("MESSAGE", NotificationType.MESSAGE),
//                defaultContent(e.getContent(), "Новое сообщение"));
//    }
//
//    private NotificationType resolveType(String name, NotificationType fallback) {
//        try {
//            return NotificationType.valueOf(name);
//        } catch (Exception ex) {
//            log.warn("NotificationType {} не найден, используем fallback={}", name, fallback);
//            return fallback;
//        }
//    }
//
//    private String defaultContent(String content, String def) {
//        return (content == null || content.isBlank()) ? def : content;
//    }
//
//    private void create(java.util.UUID authorId,
//                        java.util.UUID userId,
//                        NotificationType type,
//                        String content) {
//        try {
//            notificationService.create(
//                    NotificationsInputDto.builder()
//                            .authorId(authorId)
//                            .userId(userId)
//                            .notificationType(type)
//                            .content(content)
//                            .build()
//            );
//        } catch (Exception ex) {
//            log.error("Ошибка при создании уведомления: {}", ex.getMessage(), ex);
//        }
//    }
//
//    private <T> T parse(String json, Class<T> type) {
//        try {
//            return objectMapper.readValue(json, type);
//        } catch (Exception ex) {
//            log.error("Ошибка парсинга {}: {}", type.getSimpleName(), ex.getMessage());
//            return null;
//        }
//    }
//}