package ru.skillbox.socialnetwork.friend.friend.dto.friendship;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FriendsSearchRequest {
    
    private List<UUID> ids;

    private String firstName;

    private LocalDate birthDateFrom;

    private LocalDate birthDateTo;

    private String city;

    private String country;

    private Integer ageTo;

    private Integer ageFrom;

    private String statusCode;

}
