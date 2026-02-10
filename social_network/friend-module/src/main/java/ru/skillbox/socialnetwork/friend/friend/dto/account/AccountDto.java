package ru.skillbox.socialnetwork.friend.friend.dto.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AccountDto {

    private UUID id;

    private String email;

    private String phone;

    private String photo;

    private String about;

    private String city;

    private String country;

    private String firstName;

    private String lastName;

    private LocalDateTime regDate;

    private LocalDate birthDay;

    private LocalDateTime lastOnlineTime;

    private Boolean isOnline;

    private Boolean isBlocked;

    private Boolean isDeleted;

    private String photoName;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private LocalDateTime emojiStatus;
}
