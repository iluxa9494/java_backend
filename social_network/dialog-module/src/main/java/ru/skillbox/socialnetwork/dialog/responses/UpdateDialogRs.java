package ru.skillbox.socialnetwork.dialog.responses;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.dtos.UpdateDialogDto;

@Data
@Builder
public class UpdateDialogRs {
    String error;
    Integer timestamp;
    UpdateDialogDto data;
    String errorDescription;
}
