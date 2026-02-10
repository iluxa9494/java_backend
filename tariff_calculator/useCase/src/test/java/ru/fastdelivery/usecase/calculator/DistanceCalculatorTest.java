package ru.fastdelivery.usecase.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.geo.Coordinate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DistanceCalculatorTest {
    private final DistanceCalculator calculator = new DistanceCalculator();

    @Test
    @DisplayName("Расчет расстояния между Москвой и Санкт-Петербургом")
    void shouldCalculateCorrectDistanceBetweenTwoPoints() {
        Coordinate moscow = new Coordinate(BigDecimal.valueOf(55.7558), BigDecimal.valueOf(37.6176));
        Coordinate spb = new Coordinate(BigDecimal.valueOf(59.9343), BigDecimal.valueOf(30.3351));
        double distance = calculator.calculate(moscow, spb);
        assertEquals(634, distance, 10.0);
    }

    @Test
    @DisplayName("Расстояние между одной и той же точкой — 0")
    void shouldReturnZeroForSamePoint() {
        Coordinate point = new Coordinate(BigDecimal.valueOf(50.0), BigDecimal.valueOf(30.0));
        double distance = calculator.calculate(point, point);
        assertEquals(0.0, distance, 0.0001);
    }
}