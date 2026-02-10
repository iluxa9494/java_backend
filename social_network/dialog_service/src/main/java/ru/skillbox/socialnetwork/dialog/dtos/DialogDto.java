package ru.skillbox.socialnetwork.dialog.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DialogDto {
    UUID id;
    Integer unreadCount;
    UUID conversationPartner1;
    UUID conversationPartner2;
    MessageDto lastMessage;
}
