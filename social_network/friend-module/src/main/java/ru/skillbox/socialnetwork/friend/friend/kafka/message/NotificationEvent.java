package ru.skillbox.socialnetwork.friend.friend.kafka.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationEvent {
    private UUID eventId;
    private OffsetDateTime time;
    private UUID authorId; // кто отправил заявку
    private UUID userId; // кому адресовано
    private String content;
}
