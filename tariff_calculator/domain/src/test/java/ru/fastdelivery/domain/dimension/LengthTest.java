package ru.fastdelivery.domain.dimension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LengthTest {
    @Test
    @DisplayName("Создание Length с положительным значением")
    void shouldCreateLengthWithPositiveValue() {
        Length length = new Length(BigDecimal.valueOf(500));
        assertEquals(BigDecimal.valueOf(500), length.value());
    }

    @Test
    @DisplayName("Создание Length с нулём вызывает исключение")
    void shouldThrowExceptionForZeroLength() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Length(BigDecimal.ZERO));
        assertEquals("Длина должна быть положительной", ex.getMessage());
    }

    @Test
    @DisplayName("Сложение двух объектов Length")
    void shouldAddTwoLengthsCorrectly() {
        Length l1 = new Length(BigDecimal.valueOf(300));
        Length l2 = new Length(BigDecimal.valueOf(200));
        Length result = l1.add(l2);
        assertEquals(new Length(BigDecimal.valueOf(500)), result);
    }

    @Test
    @DisplayName("Проверка equals и hashCode")
    void shouldBeEqualForSameValue() {
        Length l1 = new Length(BigDecimal.valueOf(150));
        Length l2 = new Length(BigDecimal.valueOf(150));
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }
}