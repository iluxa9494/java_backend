package ru.skillbox.socialnetwork.account.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
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