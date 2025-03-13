package ru.skillbox.currency.exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CurrencyLoaderTest {

    @InjectMocks
    private CurrencyLoader currencyLoader;

    @Mock
    private CurrencyRepository currencyRepository;
    private RestTemplate restTemplateMock;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        restTemplateMock = mock(RestTemplate.class);

        Field restTemplateField = CurrencyLoader.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(currencyLoader, restTemplateMock);
    }

    @Test
    public void testUpdateCurrenciesSuccess_NewCurrencyCreated() throws Exception {
        String xmlResponse = """
                <ValCurs Date="12.03.2025" name="Foreign Currency Market">
                    <Valute ID="R01010">
                        <NumCode>036</NumCode>
                        <CharCode>AUD</CharCode>
                        <Nominal>1</Nominal>
                        <Name>Австралийский доллар</Name>
                        <Value>54,3380</Value>
                    </Valute>
                </ValCurs>
                """;
        when(restTemplateMock.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(xmlResponse, HttpStatus.OK));
        when(currencyRepository.findByIsoCharCode("AUD")).thenReturn(Optional.empty());

        currencyLoader.updateCurrencies();

        ArgumentCaptor<Currency> captor = ArgumentCaptor.forClass(Currency.class);
        verify(currencyRepository, times(1)).save(captor.capture());

        Currency savedCurrency = captor.getValue();
        assertEquals("AUD", savedCurrency.getIsoCharCode());
        assertEquals("Австралийский доллар", savedCurrency.getName());
        assertEquals(new BigDecimal("54.3380"), savedCurrency.getExchangeRate());
        assertEquals(1, savedCurrency.getNominal().intValue());
    }

    @Test
    public void testUpdateCurrenciesWithInvalidXml() throws Exception {
        String invalidXml = "<invalid></invalid>";

        when(restTemplateMock.getForEntity(any(String.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(invalidXml, HttpStatus.OK));
        assertDoesNotThrow(() -> currencyLoader.updateCurrencies());
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    public void testUpdateCurrencies_RestClientException() {
        when(restTemplateMock.getForEntity(any(String.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection error"));
        assertDoesNotThrow(() -> currencyLoader.updateCurrencies());
        verify(currencyRepository, never()).save(any(Currency.class));
    }
}
