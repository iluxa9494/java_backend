package ru.skillbox.socialnetwork.friend.friend.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountByFilterDto {

    private AccountSearchDto accountSearchDto;

    private Integer pageSize;

    private Integer pageNumber;
}
