package ru.skillbox.socialnetwork.authentication.events;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangedPasswordEvent {

    private String newPassword;

    private String email;

    private UUID id;
}