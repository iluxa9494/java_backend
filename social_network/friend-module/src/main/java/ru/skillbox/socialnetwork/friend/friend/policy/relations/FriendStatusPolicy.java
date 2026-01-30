package ru.skillbox.socialnetwork.friend.friend.policy.relations;

import ru.skillbox.socialnetwork.friend.friend.dto.friendship.StatusCode;

public class FriendStatusPolicy {

    public static final StatusCode DEFAULT_STATUS = StatusCode.NONE;

    // Блокировка
    public static final StatusCode STATUS_AFTER_BLOCKING = StatusCode.BLOCKED;
    public static final StatusCode STATUS_FRIEND_AFTER_BLOCKING = StatusCode.REJECTING;

    // Разблокировка
    public static final StatusCode STATUS_AFTER_UNBLOCKING = StatusCode.NONE;
    public static final StatusCode STATUS_FRIEND_AFTER_UNBLOCKING = StatusCode.NONE;

    // Заявка в друзья
    public static final StatusCode STATUS_AFTER_FRIEND_REQUEST = StatusCode.REQUEST_TO;
    public static final StatusCode STATUS_FRIEND_AFTER_FRIEND_REQUEST = StatusCode.REQUEST_FROM;

    // Принятие заявки
    public static final StatusCode STATUS_AFTER_FRIEND_APPROVE = StatusCode.FRIEND;
    public static final StatusCode STATUS_FRIEND_AFTER_FRIEND_APPROVE = StatusCode.FRIEND;

    // Удаление из друзей
    public static final StatusCode STATUS_AFTER_DELETE_FRIEND = StatusCode.SUBSCRIBED;
    public static final StatusCode STATUS_FRIEND_AFTER_DELETE_FRIEND = StatusCode.WATCHING;

    // Подписка
    public static final StatusCode STATUS_AFTER_SUBSCRIBE = StatusCode.SUBSCRIBED;
    public static final StatusCode STATUS_FRIEND_AFTER_SUBSCRIBE = StatusCode.WATCHING;

    // Отписка
    public static final StatusCode STATUS_AFTER_UNSUBSCRIBE = StatusCode.NONE;
    public static final StatusCode STATUS_FRIEND_AFTER_UNSUBSCRIBE = StatusCode.NONE;


    // Поиск
    public static final StatusCode STATUS_FOR_FINDING_IDS_FRIENDS = StatusCode.FRIEND;
    public static final StatusCode STATUS_FOR_FINDING_COUNT_FRIENDS = StatusCode.FRIEND;
    public static final StatusCode STATUS_FOR_FINDING_BLOCKED_FRIENDS = StatusCode.BLOCKED;
}