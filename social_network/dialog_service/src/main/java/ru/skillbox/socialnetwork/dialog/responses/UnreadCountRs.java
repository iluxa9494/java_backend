package ru.skillbox.socialnetwork.dialog.responses;

import lombok.Builder;
import lombok.Data;
import ru.skillbox.socialnetwork.dialog.dtos.UnreadCountDto;

@Data
@Builder
public class UnreadCountRs {
    String error;
    Integer timestamp;
    UnreadCountDto data;
    String errorDescription;
}
