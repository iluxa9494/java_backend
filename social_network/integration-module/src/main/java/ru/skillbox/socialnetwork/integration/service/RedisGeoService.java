package ru.skillbox.socialnetwork.integration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.integration.client.GeoClient;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;
import ru.skillbox.socialnetwork.integration.exception.GeoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisGeoService implements GeoService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final GeoClient geoClient;

    @Value("${geo.redis.all-country-key}")
    private String allCountryKey;

    @Value("${geo.update.min-interval-ms:3600000}")
    private long minIntervalMs;

    private final AtomicLong lastUpdateAttemptMs = new AtomicLong(0);

    @Override
    public List<Country> getCountriesWithCities() {
        log.info("Retrieving all countries with cities from Redis");
        List<Country> countries = loadFromCache();
        if (countries.isEmpty()) {
            log.debug("No countries found in Redis cache, fetching from external service");
            countries = this.update();
        }
        log.info("Successfully retrieved {} countries from Redis cache", countries.size());
        return countries;
    }

    @Override
    public List<Country> update() {
        log.info("Updating countries data from external geo service");
        List<Country> cached = loadFromCache();
        boolean hasCached = !cached.isEmpty();
        try {
            List<Country> countries = geoClient.getAllCountriesWithCities();
            if (countries == null || countries.isEmpty()) {
                if (hasCached) {
                    log.warn("Geo update returned empty list; keeping previous cache ({} countries)", cached.size());
                    return cached;
                }
                throw new GeoException("Received empty or null countries list from external geo service");
            }
            this.addAllCountriesWithCities(countries);
            return countries;
        } catch (GeoException e) {
            if (hasCached) {
                log.warn("Geo update failed; keeping previous cache ({} countries). Reason: {}", cached.size(), e.getMessage());
                return cached;
            }
            throw e;
        }
    }

    @Override
    @Scheduled(initialDelayString = "${geo.update.initial-delay-ms:10000}",
            fixedDelayString = "${geo.update.interval-ms:43200000}",
            timeUnit = TimeUnit.MILLISECONDS)
    public void autoUpdate() {
        long now = System.currentTimeMillis();
        long lastAttempt = lastUpdateAttemptMs.get();
        if (now - lastAttempt < minIntervalMs) {
            log.debug("Geo update skipped due to backoff (last attempt {} ms ago)", now - lastAttempt);
            return;
        }
        lastUpdateAttemptMs.set(now);
        log.debug("Starting scheduled auto-update of countries data");
        try {
            this.update();
        } catch (Exception e) {
            log.warn("Scheduled geo update failed: {}", e.getMessage());
        }
    }

    @Override
    public Country getCountryWithCitiesById(Integer countryId) {
        log.info("Retrieving country with cities by ID: {}", countryId);
        Country country = (Country) redisTemplate.opsForHash().get(allCountryKey, countryId.toString());
        if (country == null) {
            List<Country> countries = geoClient.getAllCountriesWithCities();
            country = countries.stream()
                    .filter(c -> c.getId().equals(countryId))
                    .findFirst().orElse(null);
            this.addAllCountriesWithCities(countries);
        } else {
            log.debug("Country found in Redis cache: ID {}", countryId);
        }
        return country;
    }

    @Override
    public void addAllCountriesWithCities(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            throw new IllegalArgumentException("Countries list cannot be null or empty");
        }

        log.info("Adding {} countries to Redis cache", countries.size());


        Map<String, Country> countriesMap = new HashMap<>();
        for (Country country : countries) {
            if (country.getId() == null) {
                log.warn("Skipping country with null ID during cache operation");
                continue;
            }
            countriesMap.put(country.getId().toString(), country);
        }
        redisTemplate.opsForHash().putAll(allCountryKey, countriesMap);
    }

    @Override
    public void addCountryWithCities(Country country) {
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
        log.info("Adding single country to Redis cache: ID {}", country.getId());
        redisTemplate.opsForHash().put(allCountryKey, country.getId().toString(), country);
    }

    @Override
    public boolean deleteCountriesWithCities() {
        log.info("Deleting all countries data from Redis cache");
        return Boolean.TRUE.equals(redisTemplate.delete(allCountryKey));
    }

    private List<Country> loadFromCache() {
        return new ArrayList<>(redisTemplate.<String, Country>opsForHash()
                .entries(allCountryKey)
                .values());
    }

}
