package ru.skillbox.socialnetwork.integration.dto.geo;

import lombok.*;

import java.util.Objects;
import java.util.TreeSet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country implements Comparable<Country> {

    private Integer id;
    private String title;
    @Builder.Default
    private TreeSet<City> cities = new TreeSet<>();

    @Override
    public int compareTo(Country country) {
        return this.title.compareTo(country.getTitle());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Country country = (Country) object;
        return Objects.equals(id, country.id) && Objects.equals(title, country.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
