package ru.skillbox.socialnetwork.post.debug;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/_debug")
@RequiredArgsConstructor
public class DebugEventController {

    // именно строковый шаблон, чтобы писать ровно тот JSON, что пришёл
    private final KafkaTemplate<String, String> kafkaTemplateString;

    @PostMapping(path = "/send-event", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String,Object>> sendEvent(
            @RequestParam String topic,
            @RequestParam(required = false) String key,
            @RequestBody String payload
    ) throws Exception {
        String k = (key != null && !key.isBlank()) ? key : UUID.randomUUID().toString();

        // ждём метаданные, чтобы вернуть partition/offset
        var result = kafkaTemplateString.send(topic, k, payload).get(3, TimeUnit.SECONDS);
        var meta = result.getRecordMetadata();

        return ResponseEntity.accepted().body(Map.of(
                "status", "SENT",
                "topic", topic,
                "key", k,
                "partition", meta.partition(),
                "offset", meta.offset(),
                "timestamp", meta.timestamp(),
                "sizeBytes", payload.getBytes().length
        ));
    }
}
