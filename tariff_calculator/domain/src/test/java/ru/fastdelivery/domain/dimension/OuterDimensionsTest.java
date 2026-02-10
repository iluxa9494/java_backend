package ru.fastdelivery.domain.dimension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OuterDimensionsTest {
    @Test
    @DisplayName("Создание с положительными значениями должно пройти успешно")
    void createWithPositiveDimensions() {
        OuterDimensions dimensions = new OuterDimensions(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(300)
        );
        assertEquals(BigDecimal.valueOf(100), dimensions.length());
        assertEquals(BigDecimal.valueOf(200), dimensions.width());
        assertEquals(BigDecimal.valueOf(300), dimensions.height());
    }

    @Test
    @DisplayName("Создание с нулевой или отрицательной длиной должно выбросить исключение")
    void createWithInvalidLength() {
        assertThrows(IllegalArgumentException.class, () ->
                new OuterDimensions(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () ->
                new OuterDimensions(BigDecimal.valueOf(-1), BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Создание с null значением должно выбросить исключение")
    void createWithNulls() {
        assertThrows(IllegalArgumentException.class, () ->
                new OuterDimensions(null, BigDecimal.ONE, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () ->
                new OuterDimensions(BigDecimal.ONE, null, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () ->
                new OuterDimensions(BigDecimal.ONE, BigDecimal.ONE, null));
    }

    @Test
    @DisplayName("Вычисление объема в мм³")
    void volumeCalculation() {
        OuterDimensions dimensions = new OuterDimensions(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(300)
        );
        BigDecimal expectedVolume = BigDecimal.valueOf(100 * 200 * 300);
        assertEquals(expectedVolume, dimensions.volume());
    }

    @Test
    @DisplayName("Вычисление объема в м³")
    void cubicMetersCalculation() {
        OuterDimensions dimensions = new OuterDimensions(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000)
        );
        BigDecimal expected = BigDecimal.valueOf(1.000000).setScale(6);
        assertEquals(expected, dimensions.cubicMeters());
    }
}
