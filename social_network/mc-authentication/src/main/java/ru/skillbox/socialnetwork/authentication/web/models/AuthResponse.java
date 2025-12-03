package ru.skillbox.socialnetwork.authentication.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private UUID id;

    private String token;

    private String refreshToken;

    private String username;

    private String email;

    private Set<String> roles;
}
