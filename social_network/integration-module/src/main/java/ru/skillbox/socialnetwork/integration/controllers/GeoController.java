package ru.skillbox.socialnetwork.integration.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.integration.dto.geo.City;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;
import ru.skillbox.socialnetwork.integration.service.GeoService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("api/v1/geo")
@RequiredArgsConstructor
public class GeoController {

    private final GeoService geoService;

    @GetMapping("/country")
    public List<Country> getCountries(@RequestParam(required = false) Boolean update) {
        log.info("Received request to get countries. Update parameter: {}", update);

        if (update != null && update) {
            log.debug("Update flag is true, refreshing countries data from external service");
            return geoService.update();
        }
        return geoService.getCountriesWithCities();
    }

    @GetMapping("/{countryId}/city")
    public Set<City> getCitiesByCountryId(@PathVariable Integer countryId) {
        log.info("Received request to get cities for country ID: {}", countryId);
        Country country = geoService.getCountryWithCitiesById(countryId);
        if (countryId == null) {
            throw new IllegalArgumentException("Country ID cannot be null");
        }

        if (countryId < 0) {
            throw new IllegalArgumentException("Country ID must be a positive integer");
        }

        if (country.getCities() == null || country.getCities().isEmpty()) {
            log.debug("Country found but no cities available for country ID: {}", countryId);
            return Set.of();
        }
        return country.getCities();
    }

}
