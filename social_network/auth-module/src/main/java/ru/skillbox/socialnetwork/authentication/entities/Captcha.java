package ru.skillbox.socialnetwork.authentication.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "captchas")
public class Captcha {

    @Id
    private String id;

    private String code;

    private Instant expiresAt;
}