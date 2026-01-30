package ru.skillbox.socialnetwork.dialog.requests;

import lombok.Data;
import ru.skillbox.socialnetwork.dialog.enums.ReadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDataRequest {
    LocalDateTime time;
    UUID conversationPartner1;
    UUID conversationPartner2;
    String messageText;
    ReadStatus readStatus;
    UUID id;
    UUID dialogId;
}
