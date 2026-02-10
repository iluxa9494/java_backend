package ru.skillbox.socialnetwork.account.dto.jwt;

import lombok.Data;

@Data
public class ValidateRequest {
    private String token;
}