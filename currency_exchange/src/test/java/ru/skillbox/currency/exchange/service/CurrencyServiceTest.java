package ru.skillbox.currency.exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class CurrencyServiceTest {

    private CurrencyRepository currencyRepository;
    private CurrencyService currencyService;

    @BeforeEach
    public void setUp() {
        currencyRepository = Mockito.mock(CurrencyRepository.class);
        currencyService = new CurrencyService(currencyRepository);
    }

    @Test
    public void testConvertCurrency_Success() {
        Currency currency = new Currency();
        currency.setId(1L);
        currency.setName("Доллар США");
        currency.setNominal(1);
        currency.setIsoNumCode(840);
        currency.setIsoCharCode("USD");
        currency.setExchangeRate(BigDecimal.valueOf(86.50));
        currency.setCreatedAt(LocalDateTime.now());

        Mockito.when(currencyRepository.findByIsoNumCode(eq(840)))
                .thenReturn(Optional.of(currency));
        BigDecimal result = currencyService.convertCurrency(BigDecimal.valueOf(100), 840);

        assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(8650));
    }

    @Test
    public void testConvertCurrency_CurrencyNotFound() {
        Mockito.when(currencyRepository.findByIsoNumCode(eq(999)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> currencyService.convertCurrency(BigDecimal.valueOf(100), 999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Валюта с кодом 999 не найдена.");
    }

    @Test
    public void testUpdateCurrencyRate_Success() {
        Currency existing = new Currency();
        existing.setId(2L);
        existing.setName("Евро");
        existing.setNominal(1);
        existing.setIsoNumCode(978);
        existing.setIsoCharCode("EUR");
        existing.setExchangeRate(BigDecimal.valueOf(93.66));
        existing.setCreatedAt(LocalDateTime.now());

        Currency updated = new Currency();
        updated.setId(2L);
        updated.setName("Евро");
        updated.setNominal(1);
        updated.setIsoNumCode(978);
        updated.setIsoCharCode("EUR");
        updated.setExchangeRate(BigDecimal.valueOf(95.00));
        updated.setCreatedAt(existing.getCreatedAt());

        Mockito.when(currencyRepository.findByIsoNumCode(eq(978)))
                .thenReturn(Optional.of(existing));
        Mockito.when(currencyRepository.save(any(Currency.class))).thenReturn(updated);

        CurrencyDto result = currencyService.updateCurrencyRate(978, BigDecimal.valueOf(95.00));

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getExchangeRate()).isEqualByComparingTo(BigDecimal.valueOf(95.00));
    }

    @Test
    public void testUpdateCurrencyRate_CurrencyNotFound() {
        Mockito.when(currencyRepository.findByIsoNumCode(eq(123)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> currencyService.updateCurrencyRate(123, BigDecimal.valueOf(100)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Валюта с кодом 123 не найдена.");
    }
}
