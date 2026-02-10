package ru.fastdelivery.usecase.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PriceConverterImplTest {
    private CurrencyRepository currencyRepository;
    private PriceConverterImpl priceConverter;

    @BeforeEach
    void setUp() {
        currencyRepository = mock(CurrencyRepository.class);
        priceConverter = new PriceConverterImpl(currencyRepository);
    }

    @Test
    @DisplayName("Если валюты одинаковые — возвращается исходная цена")
    void whenSameCurrency_thenReturnSamePrice() {
        Price price = Price.of(new BigDecimal("100"), "USD");
        Currency target = new Currency("USD", new BigDecimal("90.0"));
        Price result = priceConverter.convert(price, target);
        assertEquals(price, result);
        verifyNoInteractions(currencyRepository);
    }

    @Test
    @DisplayName("Если валюты разные — производится конвертация")
    void whenDifferentCurrency_thenConvert() {
        Price price = Price.of(new BigDecimal("100"), "USD");
        Currency sourceCurrency = new Currency("USD", new BigDecimal("90"));
        Currency targetCurrency = new Currency("EUR", new BigDecimal("100"));
        when(currencyRepository.findByCode("USD")).thenReturn(Optional.of(sourceCurrency));
        Price result = priceConverter.convert(price, targetCurrency);
        BigDecimal expected = new BigDecimal("100").multiply(new BigDecimal("90"))
                .divide(new BigDecimal("100"), 18, BigDecimal.ROUND_HALF_UP);
        assertEquals("EUR", result.getCurrencyCode());
        assertEquals(expected, result.getAmount());
    }

    @Test
    @DisplayName("Если курс исходной валюты не найден — выбрасывается исключение")
    void whenSourceCurrencyNotFound_thenThrow() {
        Price price = Price.of(new BigDecimal("100"), "JPY");
        Currency target = new Currency("RUB", new BigDecimal("1.0"));
        when(currencyRepository.findByCode("JPY")).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                priceConverter.convert(price, target)
        );
        assertEquals("Не найден курс валюты: JPY", ex.getMessage());
    }
}