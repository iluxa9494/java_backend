package ru.skillbox.socialnetwork.integration.service;

import ru.skillbox.socialnetwork.integration.dto.geo.Country;

import java.util.List;

public interface GeoService {

    List<Country> getCountriesWithCities();

    List<Country> update();

    void autoUpdate();

    Country getCountryWithCitiesById(Integer countryId);

    void addAllCountriesWithCities(List<Country> countries);

    void addCountryWithCities(Country country);

    boolean deleteCountriesWithCities();
}
