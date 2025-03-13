package ru.skillbox.currency.exchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.UpdateExchangeRateRequest;
import ru.skillbox.currency.exchange.service.CurrencyService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllCurrencies() throws Exception {
        CurrencyDto currency1 = new CurrencyDto(1L, "Доллар США", 1, 840, "USD", BigDecimal.valueOf(86.5669), LocalDateTime.now());
        CurrencyDto currency2 = new CurrencyDto(2L, "Евро", 1, 978, "EUR", BigDecimal.valueOf(93.6639), LocalDateTime.now());
        Mockito.when(currencyService.getAllCurrencies()).thenReturn(Arrays.asList(currency1, currency2));

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Доллар США")))
                .andExpect(jsonPath("$[1].name", is("Евро")));
    }

    @Test
    public void testGetCurrencyByIdFound() throws Exception {
        CurrencyDto currency = new CurrencyDto(1L, "Доллар США", 1, 840, "USD", BigDecimal.valueOf(86.5669), LocalDateTime.now());
        Mockito.when(currencyService.getCurrencyById(1L)).thenReturn(Optional.of(currency));

        mockMvc.perform(get("/api/currencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Доллар США")))
                .andExpect(jsonPath("$.isoCharCode", is("USD")));
    }

    @Test
    public void testGetCurrencyByIdNotFound() throws Exception {
        Mockito.when(currencyService.getCurrencyById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/currencies/999"))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));
    }

    @Test
    public void testGetCurrencyByIsoCodeFound() throws Exception {
        CurrencyDto currency = new CurrencyDto(2L, "Евро", 1, 978, "EUR", BigDecimal.valueOf(93.6639), LocalDateTime.now());
        Mockito.when(currencyService.getCurrencyByIsoCode(978)).thenReturn(Optional.of(currency));

        mockMvc.perform(get("/api/currencies/iso/978"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Евро")))
                .andExpect(jsonPath("$.isoCharCode", is("EUR")));
    }

    @Test
    public void testConvertCurrency() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal result = BigDecimal.valueOf(10000);
        Mockito.when(currencyService.convertCurrency(amount, 840)).thenReturn(result);

        mockMvc.perform(get("/api/currencies/convert")
                        .param("amount", "100")
                        .param("isoNumCode", "840"))
                .andExpect(status().isOk())
                .andExpect(content().string("10000"));
    }

    @Test
    public void testCreateCurrencySuccess() throws Exception {
        CurrencyDto request = new CurrencyDto(null, "Новая валюта", 1, 999, "NEW", BigDecimal.valueOf(1.2345), null);
        CurrencyDto created = new CurrencyDto(4L, "Новая валюта", 1, 999, "NEW", BigDecimal.valueOf(1.2345), LocalDateTime.now());
        Mockito.when(currencyService.createCurrency(any(CurrencyDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("Новая валюта")));
    }

    @Test
    public void testUpdateCurrencyRateSuccess() throws Exception {
        UpdateExchangeRateRequest request = new UpdateExchangeRateRequest();
        request.setExchangeRate(new BigDecimal("95.000000"));
        CurrencyDto updatedCurrency = new CurrencyDto(2L, "Евро", 1, 978, "EUR", BigDecimal.valueOf(95.0000), LocalDateTime.now());
        Mockito.when(currencyService.updateCurrencyRate(eq(978), any(BigDecimal.class))).thenReturn(updatedCurrency);

        mockMvc.perform(put("/api/currencies/978/exchangeRate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchangeRate", is(95.0000)));
    }

    @Test
    public void testDeleteCurrencySuccess() throws Exception {
        Mockito.doNothing().when(currencyService).deleteCurrency(1L);

        mockMvc.perform(delete("/api/currencies/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCurrencyError() throws Exception {
        CurrencyDto request = new CurrencyDto(null, "", 0, 0, "", BigDecimal.ZERO, null);
        Mockito.when(currencyService.createCurrency(any(CurrencyDto.class)))
                .thenThrow(new IllegalArgumentException("Некорректные данные"));

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Некорректные данные")));
    }
}