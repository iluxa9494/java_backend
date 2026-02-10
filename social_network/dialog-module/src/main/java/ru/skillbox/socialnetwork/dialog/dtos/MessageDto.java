package ru.skillbox.socialnetwork.dialog.dtos;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.enums.ReadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageDto {
    UUID id;
    UUID dialogId;
    LocalDateTime time;
    String messageText;
    UUID conversationPartner1;
    UUID conversationPartner2;
    ReadStatus readStatus;
    Boolean stubDate;
    String date;
}
