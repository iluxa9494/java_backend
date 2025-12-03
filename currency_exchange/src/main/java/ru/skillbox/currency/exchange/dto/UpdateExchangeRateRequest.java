package ru.skillbox.currency.exchange.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateExchangeRateRequest {
    private BigDecimal exchangeRate;
}
