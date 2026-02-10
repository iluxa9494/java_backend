package ru.skillbox.socialnetwork.integration.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skillbox.socialnetwork.integration.client.GeoClient;
import ru.skillbox.socialnetwork.integration.dto.geo.Country;
import ru.skillbox.socialnetwork.integration.exception.GeoException;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RedisGeoServiceTest {

    @Test
    void updateKeepsCacheWhenGeoReturnsEmpty() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        HashOperations<String, String, Country> hashOps = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn((HashOperations) hashOps);

        Country cached = Country.builder().id(1).title("Country").cities(new TreeSet<>()).build();
        when(hashOps.entries("geo:all-countries")).thenReturn(Map.of("1", cached));

        GeoClient geoClient = mock(GeoClient.class);
        when(geoClient.getAllCountriesWithCities()).thenReturn(List.of());

        RedisGeoService service = new RedisGeoService(redisTemplate, geoClient);
        ReflectionTestUtils.setField(service, "allCountryKey", "geo:all-countries");

        List<Country> result = service.update();

        assertThat(result).containsExactly(cached);
        verify(hashOps, never()).putAll(anyString(), anyMap());
    }

    @Test
    void updateThrowsWhenGeoReturnsEmptyAndCacheIsEmpty() {
        RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        HashOperations<String, String, Country> hashOps = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn((HashOperations) hashOps);
        when(hashOps.entries("geo:all-countries")).thenReturn(Map.of());

        GeoClient geoClient = mock(GeoClient.class);
        when(geoClient.getAllCountriesWithCities()).thenReturn(List.of());

        RedisGeoService service = new RedisGeoService(redisTemplate, geoClient);
        ReflectionTestUtils.setField(service, "allCountryKey", "geo:all-countries");

        assertThatThrownBy(service::update)
                .isInstanceOf(GeoException.class)
                .hasMessageContaining("empty or null countries list");
    }
}
