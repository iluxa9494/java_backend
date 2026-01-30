package ru.skillbox.socialnetwork.integration.client;

import ru.skillbox.socialnetwork.integration.dto.geo.Country;

import java.util.List;

public interface GeoClient {

    List<Country> getAllCountriesWithCities();
}
