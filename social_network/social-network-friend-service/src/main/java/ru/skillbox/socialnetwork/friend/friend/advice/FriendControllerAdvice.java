package ru.skillbox.socialnetwork.friend.friend.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.skillbox.socialnetwork.friend.friend.dto.common.Message;
import ru.skillbox.socialnetwork.friend.friend.exception.friendship.*;

@ControllerAdvice
public class FriendControllerAdvice {

    @ExceptionHandler(FriendAlreadyBlockedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Message friendAlreadyBlockedExceptionHandler(FriendAlreadyBlockedException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(FriendAlreadySentFriendRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Message friendAlreadySentFriendRequestExceptionHandler(FriendAlreadySentFriendRequestException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(FriendAlreadyUnblockedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleFriendAlreadyUnblockedException(FriendAlreadyUnblockedException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(FriendBlockedUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleFriendBlockedUserException(FriendBlockedUserException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(FriendRequestBlockedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleFriendRequestBlockedException(FriendRequestBlockedException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(FriendshipAlreadyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleFriendshipAlreadyException(FriendshipAlreadyException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(RelationsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Message handleRelationsNotFoundException(RelationsNotFoundException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(RequestAlreadyIsNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Message handleRequestAlreadyIsNotExistException(RequestAlreadyIsNotExistException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(SubscribeAlreadyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleSubscribeAlreadyException(SubscribeAlreadyException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(SubscribeBlockedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleSubscribeBlockedException(SubscribeBlockedException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(SubscribeBlockedUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleSubscribeBlockedUserException(SubscribeBlockedUserException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyIsNotFriendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Message handleUserAlreadyIsNotFriendException(UserAlreadyIsNotFriendException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(UserAlreadySentFriendRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Message handleUserAlreadySentFriendRequestException(UserAlreadySentFriendRequestException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(UserRelationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Message handleUserRelationNotFoundException(UserRelationNotFoundException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(NotSelfRequestException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Message handleNotSelfRequestException(NotSelfRequestException e) {
        return new Message(e.getMessage());
    }

}
