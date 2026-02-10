package ru.skillbox.socialnetwork.integration.dto.geo;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City implements Comparable<City> {

    private Integer id;
    private String title;
    private Integer countryId;

    @Override
    public int compareTo(@NonNull City city) {
        return this.title.compareTo(city.title);
    }
}
