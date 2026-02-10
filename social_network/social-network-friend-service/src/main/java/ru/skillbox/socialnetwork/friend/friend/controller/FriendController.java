package ru.skillbox.socialnetwork.friend.friend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.friend.common.security.SecurityUtils;
import ru.skillbox.socialnetwork.friend.friend.dto.account.AccountDto;
import ru.skillbox.socialnetwork.friend.friend.dto.common.Message;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendDto;
import ru.skillbox.socialnetwork.friend.friend.dto.friendship.FriendsSearchRequest;
import ru.skillbox.socialnetwork.friend.friend.service.FriendshipService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления отношениями между пользователями (друзьями).
 * <p>
 * Базовая реализация взаимоотношений между пользователями основана на сущности {@link ru.skillbox.socialnetwork.friend.friend.model.UserRelation}.
 * <p>
 * Отношение несимметрично. То есть, пользователь A может относиться к пользователю B как "друг",
 * а пользователь B — как "заблокированный". Отношения хранятся отдельно для каждой пары.
 */
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@Slf4j
public class FriendController {

    private final FriendshipService friendService;

    @PutMapping("/{uuid}/approve")
    @ResponseStatus(HttpStatus.OK)
    public Message approve(@PathVariable(name = "uuid") UUID friendId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} пытается принять заявку в друзья от пользователя {}", currentUserId, friendId);
        friendService.approve(currentUserId, friendId);
        log.info("Пользователь {} успешно принял заявку в друзья от пользователя {}", currentUserId, friendId);
        return new Message("Ты принял заявку в друзья");
    }

    @PutMapping("/unblock/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public Message unblock(@PathVariable(name = "uuid") UUID friendId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} пытается разблокировать пользователя {}", currentUserId, friendId);
        friendService.unblock(currentUserId, friendId);
        log.info("Пользователь {} успешно разблокировал пользователя {}", currentUserId, friendId);
        return new Message("Ты разблокировал пользователя");
    }

    @PutMapping("/block/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public Message block(@PathVariable(name = "uuid") UUID friendId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} пытается заблокировать пользователя {}", currentUserId, friendId);
        friendService.block(currentUserId, friendId);
        log.info("Пользователь {} успешно заблокировал пользователя {}", currentUserId, friendId);
        return new Message("Ты заблокировал пользователя");
    }

    @PostMapping("/{uuid}/request")
    @ResponseStatus(HttpStatus.OK)
    public Message request(@PathVariable(name = "uuid") UUID friendId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} пытается отправить заявку в друзья пользователю {}", currentUserId, friendId);
        friendService.request(currentUserId, friendId);
        log.info("Пользователь {} успешно отправил заявку в друзья пользователю {}", currentUserId, friendId);
        return new Message("Ты отправил заявку в друзья");
    }

    @PostMapping("/subscribe/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public Message subscribe(@PathVariable(name = "uuid") UUID friendId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} пытается подписаться на пользователя {}", currentUserId, friendId);
        friendService.subscribe(currentUserId, friendId);
        log.info("Пользователь {} успешно подписался на пользователя {}", currentUserId, friendId);
        return new Message("Ты подписался на пользователя");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<FriendDto> findPageFriends(@ModelAttribute FriendsSearchRequest request, Pageable pageable) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} запросил список друзей: {}", currentUserId, request);
        Page<FriendDto> result = friendService.findPageFriends(currentUserId, request, pageable);
        log.info("Получены аккаунты по statusCode {} в количестве: {}", request.getStatusCode(), result.getContent().size());
        return result;
    }

    @GetMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto findAccountByFriendId(@PathVariable(name = "uuid") UUID friendId) {
        log.info("Запрос данных аккаунта по friendId: {}", friendId);
        AccountDto account = friendService.findAccountByFriendId(friendId);
        log.info("Данные аккаунта по friendId {} успешно получены ", friendId);
        return account;
    }

    @GetMapping("/friendId")
    @ResponseStatus(HttpStatus.OK)
    public List<UUID> findFriendsId() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} запросил список ID своих друзей", currentUserId);
        List<UUID> friendIds = friendService.findFriendsId(currentUserId);
        log.info("Найдено {} друзей у пользователя {}", friendIds.size(), currentUserId);
        return friendIds;
    }

    // todo будет реализовано позже
    @GetMapping("/friendId/post/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<UUID> someMethod(@PathVariable UUID friendId) {
        log.info("Вызван временный метод someMethod с ID={}. Метод требует реализации.", friendId);
        UUID someUUID = UUID.randomUUID();
        return List.of(someUUID, someUUID);
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer findCountFriends() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} запросил количество друзей", currentUserId);
        Integer count = friendService.findCountFriends(currentUserId);
        log.info("Пользователь {} имеет {} друзей", currentUserId, count);
        return count;
    }

    @GetMapping("/blockFriendId")
    @ResponseStatus(HttpStatus.OK)
    public List<UUID> getBlockFriendIds() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} запросил список ID заблокированных друзей", currentUserId);
        List<UUID> blockedIds = friendService.getBlockFriendIds(currentUserId);
        log.info("Найдено {} заблокированных пользователей у {}", blockedIds.size(), currentUserId);
        return blockedIds;
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public Message deleteById(@PathVariable(name = "uuid") UUID friendId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Пользователь {} пытается удалить из друзей пользователя {}", currentUserId, friendId);
        friendService.deleteById(currentUserId, friendId);
        log.info("Пользователь {} успешно удалил из друзей пользователя с ID {}", currentUserId, friendId);
        return new Message("Ты удалил пользователя из друзей");
    }
}