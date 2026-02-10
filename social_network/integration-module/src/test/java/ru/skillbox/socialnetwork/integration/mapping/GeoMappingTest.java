package ru.skillbox.socialnetwork.integration.mapping;

import org.junit.jupiter.api.Test;
import ru.skillbox.socialnetwork.integration.dto.geo.AreaResponse;
import ru.skillbox.socialnetwork.integration.dto.geo.City;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GeoMappingTest {

    @Test
    void areasToCountries_collectsLeafNodesAsCities() {
        GeoMapping mapping = new GeoMapping();

        AreaResponse city1 = AreaResponse.builder()
                .id(100)
                .name("City1")
                .areas(List.of())
                .build();
        AreaResponse city2 = AreaResponse.builder()
                .id(101)
                .name("City2")
                .areas(List.of())
                .build();
        AreaResponse region = AreaResponse.builder()
                .id(10)
                .name("Region")
                .areas(List.of(city1, city2))
                .build();
        AreaResponse country = AreaResponse.builder()
                .id(1)
                .name("Country")
                .areas(List.of(region))
                .build();

        List<Country> countries = mapping.areasToCountries(List.of(country));

        assertThat(countries).hasSize(1);
        Country result = countries.get(0);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Country");

        Set<Integer> cityIds = result.getCities().stream().map(City::getId).collect(java.util.stream.Collectors.toSet());
        Set<Integer> cityCountryIds = result.getCities().stream().map(City::getCountryId).collect(java.util.stream.Collectors.toSet());

        assertThat(cityIds).containsExactlyInAnyOrder(100, 101);
        assertThat(cityCountryIds).containsExactly(1);
    }
}
