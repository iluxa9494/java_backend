package ru.skillbox.socialnetwork.dialog.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final ObjectMapper mapper = new ObjectMapper();


    public UUID extractUserId(String token) {
        Map<String, Object> payload = decodePayload(token);
        UUID userId = UUID.fromString((String) payload.get("sub"));
        return userId;
    }

    private Map<String, Object> decodePayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT");

        String payloadJson = new String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
        );
        try {
            return mapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JWT payload", e);
        }
    }
}
