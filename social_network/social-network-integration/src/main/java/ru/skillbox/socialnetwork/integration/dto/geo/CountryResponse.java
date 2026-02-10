package ru.skillbox.socialnetwork.integration.dto.geo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryResponse {

    private Integer id;
    private String name;
    private String url;
}
