package com.team58.mc_notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSettingsResponse {
    private UUID id;
    private UUID userId;
    private boolean friendRequest;
    private boolean friendBirthday;
    private boolean postComment;
    private boolean commentComment;
    private boolean post;
    private boolean message;
    private boolean sendEmailMessage;
}