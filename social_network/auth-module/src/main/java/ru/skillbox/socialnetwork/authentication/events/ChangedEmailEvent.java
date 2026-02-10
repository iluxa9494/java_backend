package ru.skillbox.socialnetwork.authentication.events;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangedEmailEvent {

    private String password;

    private String newEmail;

    private UUID id;
}