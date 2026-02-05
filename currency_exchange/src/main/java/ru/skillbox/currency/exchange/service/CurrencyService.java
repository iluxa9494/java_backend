package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyShortDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Transactional(readOnly = true)
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CurrencyShortDto> getAllShortCurrencies() {
        return currencyRepository.findAll().stream()
                .map(currency -> new CurrencyShortDto(currency.getName(), currency.getExchangeRate()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CurrencyDto> getCurrencyById(Long id) {
        return currencyRepository.findById(id).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<CurrencyDto> getCurrencyByIsoCode(Integer isoNumericCode) {
        return currencyRepository.findByIsoNumCode(isoNumericCode)
                .map(this::convertToDto);
    }

    @Transactional
    public CurrencyDto createCurrency(CurrencyDto currencyDto) {
        Currency currency = new Currency();
        currency.setName(currencyDto.getName());
        currency.setNominal(currencyDto.getNominal());
        currency.setIsoNumCode(currencyDto.getIsoNumCode());
        currency.setIsoCharCode(currencyDto.getIsoCharCode());
        currency.setExchangeRate(currencyDto.getExchangeRate());

        Currency savedCurrency = currencyRepository.save(currency);
        return convertToDto(savedCurrency);
    }

    @Transactional(readOnly = true)
    public BigDecimal convertCurrency(BigDecimal amount, Integer isoNumericCode) {
        Currency currency = currencyRepository.findByIsoNumCode(isoNumericCode)
                .orElseThrow(() -> new IllegalArgumentException("Валюта с кодом " + isoNumericCode + " не найдена."));
        if (currency == null) {
            throw new IllegalArgumentException("Валюта с кодом " + isoNumericCode + " не найдена.");
        }
        return amount.multiply(currency.getExchangeRate());
    }

    @Transactional
    public CurrencyDto updateCurrencyRate(Integer isoNumericCode, BigDecimal newExchangeRate) {
        Currency currency = currencyRepository.findByIsoNumCode(isoNumericCode)
                .orElse(null);
        if (currency == null) {
            throw new IllegalArgumentException("Валюта с кодом " + isoNumericCode + " не найдена.");
        }
        currency.setExchangeRate(newExchangeRate);
        Currency updatedCurrency = currencyRepository.save(currency);
        return convertToDto(updatedCurrency);
    }

    @Transactional
    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }

    private CurrencyDto convertToDto(Currency currency) {
        return new CurrencyDto(
                currency.getId(),
                currency.getName(),
                currency.getNominal(),
                currency.getIsoNumCode(),
                currency.getIsoCharCode(),
                currency.getExchangeRate(),
                currency.getCreatedAt()
        );
    }
}
