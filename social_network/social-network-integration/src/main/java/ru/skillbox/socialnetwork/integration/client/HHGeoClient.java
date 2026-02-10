package ru.skillbox.socialnetwork.integration.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.socialnetwork.integration.dto.geo.AreaResponse;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;
import ru.skillbox.socialnetwork.integration.dto.geo.CountryResponse;
import ru.skillbox.socialnetwork.integration.exception.GeoException;
import ru.skillbox.socialnetwork.integration.mapping.GeoMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HHGeoClient implements GeoClient {

    private final RestTemplate restTemplate;
    private final GeoMapping mapping;

    @Value("${geo.base-country-url}")
    private String baseUrl;

    @Override
    public List<Country> getAllCountriesWithCities() {
        log.info("Starting to fetch all countries with cities from external geo service");

        try {
            ResponseEntity<CountryResponse[]> response = restTemplate.getForEntity(baseUrl, CountryResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new GeoException("Failed to fetch countries from geo service. Response status: " + response.getStatusCode());
            }

            if (response.getBody() == null) {
                throw new GeoException("Received empty response body from geo service");
            }

            List<CountryResponse> countryResponses = Arrays.asList(response.getBody());
            log.debug("Successfully fetched {} countries from geo service", countryResponses.size());

            List<Country> countries = countryResponses.stream()
                    .map(this::fetchAreaDataForCountry)
                    .filter(Objects::nonNull)
                    .map(areaResponse -> mapping.areaResponseToCountry(areaResponse)
                            .orElseThrow(() -> new GeoException("Failed to map area response to country")))
                    .toList();
            log.info("Successfully processed {} countries with cities", countries.size());

            return countries;

        } catch (RestClientException e) {
            throw new GeoException("Network error while communicating with geo service: " + e.getMessage(), e);
        }
    }

    private AreaResponse fetchAreaDataForCountry(CountryResponse countryResponse) {
        String areaUrl = countryResponse.getUrl();

        if (areaUrl == null || areaUrl.isBlank()) {
            log.warn("Skipping country with null or empty area URL: {}", countryResponse.getName());
            return null;
        }

        try {
            log.debug("Fetching area data from URL: {}", areaUrl);
            ResponseEntity<AreaResponse> areaResponse = restTemplate.getForEntity(areaUrl, AreaResponse.class);

            if (!areaResponse.getStatusCode().is2xxSuccessful()) {
                log.warn("Failed to fetch area data for country {}. Response status: {}",
                        countryResponse.getName(), areaResponse.getStatusCode());
                return null;
            }

            if (areaResponse.getBody() == null) {
                log.warn("Received empty area data for country: {}", countryResponse.getName());
                return null;
            }

            log.debug("Successfully fetched area data for country: {}", countryResponse.getName());
            return areaResponse.getBody();

        } catch (RestClientException e) {
            log.warn("Network error while fetching area data for country {}: {}",
                    countryResponse.getName(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Unexpected error while processing area data for country {}: {}",
                    countryResponse.getName(), e.getMessage());
            return null;
        }
    }
}
