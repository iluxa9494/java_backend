package ru.skillbox.socialnetwork.dialog.requests;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageRequest {
    String type;
    UUID recipientId;
    MessageDataRequest data;
}
