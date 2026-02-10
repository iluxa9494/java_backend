package ru.skillbox.socialnetwork.authentication.web.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RecoveryPassportRequest {
    private String email;
}