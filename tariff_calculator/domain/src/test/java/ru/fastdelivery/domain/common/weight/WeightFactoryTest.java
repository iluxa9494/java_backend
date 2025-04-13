package ru.fastdelivery.domain.common.weight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WeightFactoryTest {

    @ParameterizedTest(name = "Граммы = {arguments} -> объект создан")
    @ValueSource(longs = { 0, 1, 100, 10_000 })
    void whenGramsGreaterThanZero_thenObjectCreated(long amount) {
        var weight = new Weight(BigInteger.valueOf(amount));

        assertNotNull(weight);
        assertThat(weight.weightGrams()).isEqualByComparingTo(BigInteger.valueOf(amount));
    }

    @ParameterizedTest(name = "Стоимость = {arguments} -> исключение")
    @ValueSource(longs = { -1, -100, -10_000 })
    @DisplayName("Значение стоимости ниже 0.00 -> исключение")
    void whenGramsLessThanZero_thenThrowException(long amount) {
        assertThatThrownBy(() -> new Weight(BigInteger.valueOf(amount)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}