package ru.skillbox.socialnetwork.integration.mapping;

import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.integration.dto.geo.AreaResponse;
import ru.skillbox.socialnetwork.integration.dto.geo.City;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

@Component
public class GeoMapping {

    public List<Country> areasToCountries(List<AreaResponse> areas) {
        if (areas == null || areas.isEmpty()) {
            return List.of();
        }
        List<Country> countries = new ArrayList<>();
        for (AreaResponse area : areas) {
            areaResponseToCountry(area).ifPresent(countries::add);
        }
        return countries;
    }

    public Optional<Country> areaResponseToCountry(AreaResponse areaResponse) {
        return Optional.ofNullable(areaResponse)
                .map(this::areaResponseToCountryWithoutCities);
    }

    private Country areaResponseToCountryWithoutCities(AreaResponse areaResponse) {
        return Country.builder()
                .id(areaResponse.getId())
                .title(areaResponse.getName())
                .cities(areaResponseToCities(areaResponse, areaResponse.getId()))
                .build();
    }

    private City areaResponseToCity(AreaResponse areaResponse, Integer countryId) {
        return City.builder()
                .id(areaResponse.getId())
                .title(areaResponse.getName())
                .countryId(countryId)
                .build();
    }

    private TreeSet<City> areaResponseToCities(AreaResponse areaResponse, Integer countryId) {
        if (areaResponse.getAreas() == null || areaResponse.getAreas().isEmpty()) {
            return new TreeSet<>();
        }
        TreeSet<City> cities = new TreeSet<>();
        processAreaResponseToCitiesRecursively(areaResponse, cities, countryId);
        return cities;
    }

    private void processAreaResponseToCitiesRecursively(AreaResponse areaResponse, TreeSet<City> cities, Integer countryId) {
        List<AreaResponse> areas = areaResponse.getAreas();
        if (areas == null || areas.isEmpty()) {
            cities.add(areaResponseToCity(areaResponse, countryId));
        } else {
            areas.forEach(area -> processAreaResponseToCitiesRecursively(area, cities, countryId));
        }
    }
}
