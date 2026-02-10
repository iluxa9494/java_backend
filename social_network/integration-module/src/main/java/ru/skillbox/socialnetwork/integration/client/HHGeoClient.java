package ru.skillbox.socialnetwork.integration.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.socialnetwork.integration.dto.geo.AreaResponse;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;
import ru.skillbox.socialnetwork.integration.exception.GeoException;
import ru.skillbox.socialnetwork.integration.mapping.GeoMapping;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HHGeoClient implements GeoClient {

    private final RestTemplate restTemplate;
    private final GeoMapping mapping;
    private final ObjectMapper objectMapper;

    @Value("${geo.base-country-url}")
    private String baseUrl;

    @Override
    public List<Country> getAllCountriesWithCities() {
        log.info("Starting to fetch all countries with cities from external geo service");

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                logBadResponse("Geo service returned non-2xx", baseUrl, response);
                throw new GeoException("Failed to fetch areas from geo service. Response status: " + response.getStatusCode());
            }

            String body = response.getBody();
            if (body == null || body.isBlank()) {
                log.warn("Geo service returned empty body. url={}", baseUrl);
                throw new GeoException("Received empty response body from geo service");
            }

            AreaResponse[] areaResponses = parseAreas(body, baseUrl, response.getHeaders().getContentType());
            List<Country> countries = mapping.areasToCountries(Arrays.asList(areaResponses));
            log.info("Successfully processed {} countries with cities", countries.size());
            return countries;
        } catch (HttpStatusCodeException e) {
            logBadResponse("Geo service HTTP error", baseUrl, e.getStatusCode().value(),
                    e.getResponseHeaders() != null ? e.getResponseHeaders().getContentType() : null,
                    e.getResponseBodyAsString());
            throw new GeoException("Failed to fetch areas from geo service. HTTP status: " + e.getStatusCode(), e);
        } catch (RestClientException e) {
            throw new GeoException("Network error while communicating with geo service: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new GeoException("Failed to parse geo service response: " + e.getMessage(), e);
        }
    }

    private AreaResponse[] parseAreas(String body, String url, MediaType contentType) throws JsonProcessingException {
        try {
            return objectMapper.readValue(body, AreaResponse[].class);
        } catch (JsonProcessingException e) {
            logBadResponse("Failed to parse geo response JSON", url, 200, contentType, body);
            throw e;
        }
    }

    private void logBadResponse(String message, String url, ResponseEntity<String> response) {
        logBadResponse(message, url, response.getStatusCode().value(),
                response.getHeaders().getContentType(), response.getBody());
    }

    private void logBadResponse(String message, String url, int status, MediaType contentType, String body) {
        String snippet = snippet(body, 500);
        String ct = contentType != null ? contentType.toString() : "unknown";
        log.warn("{}: url={} status={} contentType={} bodySnippet={}", message, url, status, ct, snippet);
    }

    private String snippet(String body, int maxLen) {
        if (body == null) {
            return "";
        }
        String trimmed = body.replaceAll("[\\r\\n\\t]+", " ").trim();
        if (trimmed.length() <= maxLen) {
            return trimmed;
        }
        return trimmed.substring(0, maxLen) + "...";
    }
}
