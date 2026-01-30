package ru.skillbox.socialnetwork.dialog.responses;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.dtos.DialogDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GetDialogsRs {
    String error;
    String errorDescription;
    Integer timestamp;
    Integer total;
    Integer offset;
    Integer perPage;
    UUID id;
    List<DialogDto> content;
}
