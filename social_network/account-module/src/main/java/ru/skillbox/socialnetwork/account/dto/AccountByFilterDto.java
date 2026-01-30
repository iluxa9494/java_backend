package ru.skillbox.socialnetwork.account.dto;

import lombok.Data;

@Data
public class AccountByFilterDto {
    private AccountSearchDto accountSearchDto;
    private Integer pageSize;
    private Integer pageNumber;
}