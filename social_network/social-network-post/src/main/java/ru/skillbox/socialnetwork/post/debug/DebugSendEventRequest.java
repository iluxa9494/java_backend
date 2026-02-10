package ru.skillbox.socialnetwork.post.debug;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public record DebugSendEventRequest(
        @NotNull Topic topic,                 // POST | COMMENT | REACTION
        String key,                           // optional: если не задан, сгенерируем UUID
        Map<String, Object> payload           // произвольный JSON, уйдёт как value продюсера
) {
    public enum Topic { POST, COMMENT, REACTION }

    @JsonCreator
    public DebugSendEventRequest(
            @JsonProperty("topic") Topic topic,
            @JsonProperty("key") String key,
            @JsonProperty("payload") Map<String, Object> payload) {
        this.topic = topic;
        this.key = key;
        this.payload = Optional.ofNullable(payload).orElse(Map.of());
    }
}