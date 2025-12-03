package ru.fastdelivery.domain.dimension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class VolumeTest {
    @Test
    @DisplayName("Объект Volume создается с положительным значением")
    void shouldCreateVolumeWithPositiveValue() {
        Volume volume = new Volume(BigDecimal.valueOf(1.23));
        assertEquals("1.23 м³", volume.toString());
    }

    @Test
    @DisplayName("Объект Volume с нулевым значением создается корректно")
    void shouldCreateVolumeWithZero() {
        Volume volume = new Volume(BigDecimal.ZERO);
        assertEquals("0 м³", volume.toString());
    }

    @Test
    @DisplayName("Исключение при создании объема с отрицательным значением")
    void shouldThrowExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Volume(BigDecimal.valueOf(-1)));
    }

    @Test
    @DisplayName("Равные объекты Volume считаются эквивалентными")
    void shouldConsiderEqualVolumes() {
        Volume v1 = new Volume(BigDecimal.valueOf(5.0));
        Volume v2 = new Volume(BigDecimal.valueOf(5.0));
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    @DisplayName("Неравные объекты Volume считаются разными")
    void shouldConsiderUnequalVolumes() {
        Volume v1 = new Volume(BigDecimal.valueOf(5.0));
        Volume v2 = new Volume(BigDecimal.valueOf(10.0));
        assertNotEquals(v1, v2);
    }
}