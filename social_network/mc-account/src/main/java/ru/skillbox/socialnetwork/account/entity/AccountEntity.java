package ru.skillbox.socialnetwork.account.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String email;

    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String firstName;
    private String lastName;

    // Помечаем как @Transient — НЕ сохраняется в БД
    @Transient
    @Setter
    @Getter
    private String password;

    @Column(name = "birth_date", columnDefinition = "DATE")
    private LocalDate birthDate;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "photo_name")
    private String photoName;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "emoji_status")
    private String emojiStatus;

    @Column(name = "profile_cover")
    private String profileCover;
}