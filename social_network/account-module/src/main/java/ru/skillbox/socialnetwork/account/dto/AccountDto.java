package ru.skillbox.socialnetwork.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.skillbox.socialnetwork.account.config.CustomLocalDateDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;  // ✅ Меняем String на UUID

    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String firstName;
    private String lastName;

    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd['T'HH:mm:ss.SSS'Z']")
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate birthDate;

    // ✅ Формат как в документации: "2025-10-31T12:02:49.740Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime regDate;

    // ✅ Формат как в документации: "2025-10-31T12:02:49.740Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastOnlineTime;

    private Boolean isBlocked;
    private Boolean isDeleted;
    private String photoName;

    // ✅ Формат как в документации: "2025-10-31T12:02:49.740Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdOn;

    // ✅ Формат как в документации: "2025-10-31T12:02:49.740Z"
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedOn;

    private String emojiStatus;
    private String profileCover;

    @JsonProperty("isOnline")
    public boolean isOnline() {
        if (this.lastOnlineTime == null) {
            return false;
        }
        return Duration.between(this.lastOnlineTime, LocalDateTime.now())
                .toMinutes() < 5;
    }
}