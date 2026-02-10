package ru.skillbox.socialnetwork.friend.friend.dto.account;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AccountSearchDto {
    private List<UUID> ids;
    private String author;
    private String firstName;
    private String lastName;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;
    private String city;
    private String country;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private Integer ageFrom;
    private Integer ageTo;
}