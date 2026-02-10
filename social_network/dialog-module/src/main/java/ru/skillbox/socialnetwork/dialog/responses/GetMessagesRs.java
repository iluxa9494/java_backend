package ru.skillbox.socialnetwork.dialog.responses;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.dtos.MessageDto;

import java.util.List;

@Data
@Builder
public class GetMessagesRs {
    String error;
    String errorDescription;
    Integer timestamp;
    Integer total;
    Integer totalPages;
    Integer perPage;
    Integer offset;
    Integer unreadCount;
    List<MessageDto> content;
}
