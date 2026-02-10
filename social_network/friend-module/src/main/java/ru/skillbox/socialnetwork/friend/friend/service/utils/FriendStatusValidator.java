package ru.skillbox.socialnetwork.friend.friend.service.utils;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendshipPairDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.StatusCode;
import ru.skillbox.socialnetwork.friend.friend.exception.friendship.*;
import ru.skillbox.socialnetwork.friend.friend.model.FriendshipPair;

@Component
public class FriendStatusValidator {

    public static void checkBeforeSendingFriendRequest(FriendshipPairDto pair) {

        switch (pair.userToFriend().getStatusCode()) {
            case BLOCKED:
                throw new FriendRequestBlockedException("Невозможно добавить в друзья заблокированного пользователя");
            case FRIEND:
                throw new FriendshipAlreadyException("Целевой пользователь уже является другом");
            case REQUEST_FROM:
                throw new FriendAlreadySentFriendRequestException("Целевой пользователь уже отправил приглашение в друзья текущему пользователю");
            case REQUEST_TO:
                throw new UserAlreadySentFriendRequestException("Текущий пользователь уже отправил приглашение в друзья целевому пользователю");
            case REJECTING:
                throw new FriendRequestBlockedException("Запрос отклонен. Целевой пользователь заблокировал отправителя запроса");
            default:
                break;
        }
    }

    public static void checkBeforeBlockingFriend(FriendshipPairDto pair) {
        if (pair.userToFriend().getStatusCode() == StatusCode.BLOCKED) {
            throw new FriendAlreadyBlockedException("Целевой пользователь уже заблокирован");
        }
    }

    public static void checkBeforeUnblockingFriend(FriendshipPairDto pair) {
        if (pair.userToFriend().getStatusCode() != StatusCode.BLOCKED) {
            throw new FriendAlreadyUnblockedException("Целевой пользователь уже разблокирован");
        }
    }

    public static void checkBeforeApprovingFriendRequest(FriendshipPair pair) {

        if (pair.userToFriend().getStatusCode() != StatusCode.REQUEST_FROM) {
            throw new RequestAlreadyIsNotExistException("Запрос на добавление в друзья не существует");
        }
    }

    public static void checkBeforeSubscribe(FriendshipPairDto pair) {

        switch (pair.userToFriend().getStatusCode()) {
            case BLOCKED:
                throw new SubscribeBlockedException("Невозможно подписаться на заблокированного пользователя");
            case FRIEND:
                throw new SubscribeAlreadyException("Невозможно стать подписчиком друга");
            case REJECTING:
                throw new SubscribeBlockedException("Невозможно подписаться, пользователь заблокировал Вас");
            case WATCHING:
                throw new SubscribeAlreadyException("Невозможно стать подписчиком подписчика");
            default:
                break;
        }

        switch (pair.friendToUser().getStatusCode()) {
            case BLOCKED:
                throw new SubscribeBlockedUserException("Целевой пользователь заблокировал отправителя запроса");
            case FRIEND:
                throw new SubscribeAlreadyException("Невозможно стать подписчиком друга");
            default:
                break;
        }
    }

    public static void checkBeforeDelete(FriendshipPairDto pair) {
        // методы какой-то костыльный, кроме удаления ещё много обязанностей поэтому валдиацию прорабатывать отдельно
    }
}