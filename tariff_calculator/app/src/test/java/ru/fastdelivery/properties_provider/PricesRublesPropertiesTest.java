package ru.fastdelivery.properties_provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.properties.provider.PricesRublesProperties;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PricesRublesPropertiesTest {

    public static final BigDecimal PER_KG = BigDecimal.valueOf(50);
    public static final BigDecimal MINIMAL = BigDecimal.valueOf(100);
    public static final String RUB = "RUB";
    final CurrencyFactory currencyFactory = mock(CurrencyFactory.class);
    PricesRublesProperties properties;

    @BeforeEach
    void init(){
        properties = new PricesRublesProperties();
        properties.setCurrencyFactory(currencyFactory);

        properties.setPerKg(PER_KG);
        properties.setMinimal(MINIMAL);

        var currency = mock(Currency.class);
        when(currency.getCode()).thenReturn(RUB);

        when(currencyFactory.create(RUB)).thenReturn(currency);
    }

    @Test
    void whenCallPricePerKg_thenRequestFromConfig() {
        var actual = properties.costPerKg();

        verify(currencyFactory).create("RUB");
        assertThat(actual.amount()).isEqualByComparingTo(PER_KG);
        assertThat(actual.currency().getCode()).isEqualTo("RUB");
    }

    @Test
    void whenCallMinimalPrice_thenRequestFromConfig() {
        var actual = properties.minimalPrice();

        verify(currencyFactory).create("RUB");
        assertThat(actual.amount()).isEqualByComparingTo(MINIMAL);
        assertThat(actual.currency().getCode()).isEqualTo("RUB");
    }
}