package ru.fastdelivery.domain.geo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTest {
    @Test
    @DisplayName("Создание координаты и получение значений")
    void createCoordinateAndAccessors() {
        BigDecimal lat = new BigDecimal("55.7558");
        BigDecimal lon = new BigDecimal("37.6176");
        Coordinate coordinate = new Coordinate(lat, lon);
        assertEquals(lat.doubleValue(), coordinate.lat());
        assertEquals(lon.doubleValue(), coordinate.lon());
        assertEquals(lat, coordinate.latRaw());
        assertEquals(lon, coordinate.lonRaw());
    }

    @Test
    @DisplayName("equals и hashCode работают корректно")
    void equalsAndHashCode() {
        Coordinate c1 = new Coordinate(new BigDecimal("50.0"), new BigDecimal("40.0"));
        Coordinate c2 = new Coordinate(new BigDecimal("50.0"), new BigDecimal("40.0"));
        Coordinate c3 = new Coordinate(new BigDecimal("51.0"), new BigDecimal("40.0"));
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
    }

    @Test
    @DisplayName("toString содержит значения координат")
    void toStringShouldContainCoordinates() {
        Coordinate coordinate = new Coordinate(new BigDecimal("60.0"), new BigDecimal("70.0"));
        String str = coordinate.toString();
        assertTrue(str.contains("60.0"));
        assertTrue(str.contains("70.0"));
    }
}