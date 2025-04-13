package ru.fastdelivery.domain.common.weight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeightTest {

    @Test
    @DisplayName("Попытка создать отрицательный вес -> исключение")
    void whenGramsBelowZero_thenException() {
        var weightGrams = new BigInteger("-1");
        assertThatThrownBy(() -> new Weight(weightGrams))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalsTypeWidth_same() {
        var weight = new Weight(new BigInteger("1000"));
        var weightSame = new Weight(new BigInteger("1000"));

        assertThat(weight)
                .isEqualTo(weightSame)
                .hasSameHashCodeAs(weightSame);
    }

    @Test
    void equalsNull_false() {
        var weight = new Weight(new BigInteger("4"));

        assertThat(weight).isNotEqualTo(null);
    }

    @ParameterizedTest
    @CsvSource({ "1000, 1, -1",
            "199, 199, 0",
            "50, 999, 1" })
    void compareToTest(BigInteger low, BigInteger high, int expected) {
        var weightLow = new Weight(low);
        var weightHigh = new Weight(high);

        assertThat(weightLow.compareTo(weightHigh))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Добавление положительного веса -> вес увеличился")
    void whenAddPositiveWeight_thenWeightIsIncreased() {
        var weightBase = new Weight(new BigInteger("1000"));
        var actual = weightBase.add(new Weight(new BigInteger("1000")));

        assertThat(actual)
                .isEqualTo(new Weight(new BigInteger("2000")));
    }

    @Test
    @DisplayName("Первый вес больше второго -> true")
    void whenFirstWeightGreaterThanSecond_thenTrue() {
        var weightBig = new Weight(new BigInteger("1001"));
        var weightSmall = new Weight(new BigInteger("1000"));

        assertThat(weightBig.greaterThan(weightSmall)).isTrue();
    }

    @Test
    @DisplayName("Запрос количество кг -> получено корректное значение")
    void whenGetKilograms_thenReceiveKg() {
        var weight = new Weight(new BigInteger("1001"));

        var actual = weight.kilograms();

        assertThat(actual).isEqualByComparingTo(new BigDecimal("1.001"));
    }
}