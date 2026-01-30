package ru.skillbox.socialnetwork.friend.friend.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountStatus;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsBlockedException;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsDeletedException;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsNotFoundException;

@Slf4j
@Component
public class AccountStatusValidator {

    public static void checkBeforeApprovingFriendRequest(AccountStatus status) {
        if (status.isBlocked()) {
            throw new AccountStatusIsBlockedException("Невозможно принять запрос в друзья, пользователь заблокирован администрацией. UserId: " + status.id());
        }
        if (status.isDeleted()) {
            throw new AccountStatusIsDeletedException("Невозможно принять запрос в друзья, пользователь удалён. UserId: " + status.id());
        }
        if (status.isNotFound()) {
            throw new AccountStatusIsNotFoundException("Невозможно принять запрос в друзья, пользователь не найден. UserId: " + status.id());
        }
    }

    public static void checkBeforeUnblockingRequest(AccountStatus status) {
        if (status.isNotFound()) {
            throw new AccountStatusIsNotFoundException("Невозможно разблокировать пользователя, так как он не найден. UserId: " + status.id());
        }
    }

    public static void checkBeforeBlockingRequest(AccountStatus status) {
        if (status.isNotFound()) {
            throw new AccountStatusIsNotFoundException("Невозможно заблокировать пользователя, так как он не найден. UserId: " + status.id());
        }
    }

    public static void checkBeforeFriendRequestSending(AccountStatus status) {
        if (status.isBlocked()) {
            throw new AccountStatusIsBlockedException("Невозможно отправить запрос в друзья, пользователь заблокирован администрацией. UserId: " + status.id());
        }
        if (status.isDeleted()) {
            throw new AccountStatusIsDeletedException("Невозможно отправить запрос в друзья, пользователь удалён. UserId: " + status.id());
        }
        if (status.isNotFound()) {
            throw new AccountStatusIsNotFoundException("Невозможно отправить запрос в друзья, пользователь не найден. UserId: " + status.id());
        }
    }

    public static void checkBeforeSubscribeRequest(AccountStatus status) {
        if (status.isBlocked()) {
            throw new AccountStatusIsBlockedException("Невозможно подписаться на пользователя, пользователь заблокирован администрацией. UserId: " + status.id());
        }
        if (status.isDeleted()) {
            throw new AccountStatusIsDeletedException("Невозможно подписаться на пользователя, пользователь удалён. UserId: " + status.id());
        }
        if (status.isNotFound()) {
            throw new AccountStatusIsNotFoundException("Невозможно подписаться на пользователя, пользователь не найден. UserId: " + status.id());
        }
    }

    public static void checkBeforeDeletingFriend(AccountStatus status) {
        if (status.isNotFound()) {
            throw new AccountStatusIsNotFoundException("Невозможно удалить пользователя из списка друзей, пользователь не найден. UserId: " + status.id());
        }
    }
}
