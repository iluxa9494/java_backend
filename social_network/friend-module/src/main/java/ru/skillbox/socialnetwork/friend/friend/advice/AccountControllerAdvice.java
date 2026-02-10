package ru.skillbox.socialnetwork.friend.friend.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.skillbox.socialnetwork.friend.friend.dto.common.Message;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsBlockedException;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsDeletedException;
import ru.skillbox.socialnetwork.friend.friend.exception.account.AccountStatusIsNotFoundException;

@ControllerAdvice
public class AccountControllerAdvice {

    @ExceptionHandler(AccountStatusIsBlockedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Message handleAccountStatusIsBlockedException(AccountStatusIsBlockedException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(AccountStatusIsDeletedException.class)
    @ResponseStatus(HttpStatus.GONE)
    public Message handleAccountStatusIsDeletedException(AccountStatusIsDeletedException e) {
        return new Message(e.getMessage());
    }

    @ExceptionHandler(AccountStatusIsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Message handleAccountStatusIsNotFoundException(AccountStatusIsNotFoundException e) {
        return new Message(e.getMessage());
    }


}
