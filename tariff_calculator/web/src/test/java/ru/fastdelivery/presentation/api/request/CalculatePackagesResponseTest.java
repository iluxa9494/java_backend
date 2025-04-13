package ru.fastdelivery.presentation.api.request;

import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatePackagesResponseTest {

    @Test
    @DisplayName("Если валюты разные -> ошибка создания объекта")
    void whenCurrenciesAreNotEqual_thenException() {
        var calculatedPrice = new Price(new BigDecimal(100), new CurrencyFactory(code -> true).create("USD"));
        var minimalPrice = new Price(new BigDecimal(5), new CurrencyFactory(code -> true).create("RUB"));

        assertThrows(IllegalArgumentException.class,
                () -> new CalculatePackagesResponse(calculatedPrice, minimalPrice));
    }

    @Test
    @DisplayName("Если валюты одинаковые -> объект создан")
    void whenCurrenciesAreEqual_thenObjectCreated() {
        var usd = new CurrencyFactory(code -> true).create("USD");
        var calculatedPrice = new Price(new BigDecimal(100), usd);
        var minimalPrice = new Price(new BigDecimal(5), usd);

        var expected = new CalculatePackagesResponse(
                new BigDecimal(100), new BigDecimal(5), usd.getCode());

        var actual = new CalculatePackagesResponse(calculatedPrice, minimalPrice);

        assertThat(actual).usingRecursiveComparison()
                .withComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .isEqualTo(expected);
    }

}