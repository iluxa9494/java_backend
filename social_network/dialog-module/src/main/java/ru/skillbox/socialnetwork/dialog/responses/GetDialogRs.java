package ru.skillbox.socialnetwork.dialog.responses;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.dtos.MessageDto;

import java.util.UUID;

@Data
@Builder
public class GetDialogRs {
    UUID id;
    Integer unreadCount;
    UUID conversationPartner1;
    UUID conversationPartner2;
    MessageDto lastMessage;
}
