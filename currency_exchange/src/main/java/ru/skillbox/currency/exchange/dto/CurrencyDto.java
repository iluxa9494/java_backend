package ru.skillbox.currency.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {
    private Long id;
    private String name;
    private Integer nominal;
    private Integer isoNumCode;
    private String isoCharCode;
    private BigDecimal exchangeRate;
    private LocalDateTime createdAt;
}
