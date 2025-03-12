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

    /**
     * Получить список всех валют.
     *
     * @return список валют в виде DTO.
     */
    @Transactional(readOnly = true)
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить список всех валют в формате CurrencyShortDto.
     *
     * @return список валют (только name и exchangeRate).
     */
    @Transactional(readOnly = true)
    public List<CurrencyShortDto> getAllShortCurrencies() {
        return currencyRepository.findAll().stream()
                .map(currency -> new CurrencyShortDto(currency.getName(), currency.getExchangeRate()))
                .collect(Collectors.toList());
    }

    /**
     * Получить валюту по ID.
     *
     * @param id идентификатор валюты.
     * @return DTO объекта валюты.
     */
    @Transactional(readOnly = true)
    public Optional<CurrencyDto> getCurrencyById(Long id) {
        return currencyRepository.findById(id).map(this::convertToDto);
    }

    /**
     * Получить валюту по числовому коду (ISO).
     *
     * @param isoNumericCode числовой код валюты.
     * @return DTO объекта валюты.
     */
    @Transactional(readOnly = true)
    public Optional<CurrencyDto> getCurrencyByIsoCode(Integer isoNumericCode) {
        return currencyRepository.findByIsoNumCode(isoNumericCode)
                .map(this::convertToDto);
    }

    /**
     * Создать новую запись валюты.
     *
     * @param currencyDto DTO с данными валюты.
     * @return созданная валюта.
     */
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

    /**
     * Конвертирует сумму из одной валюты в другую.
     *
     * @param amount         сумма для конвертации.
     * @param isoNumericCode числовой код валюты.
     * @return сконвертированная сумма.
     */
    @Transactional(readOnly = true)
    public BigDecimal convertCurrency(BigDecimal amount, Integer isoNumericCode) {
        Currency currency = currencyRepository.findByIsoNumCode(isoNumericCode)
                .orElseThrow(() -> new IllegalArgumentException("Валюта с кодом " + isoNumericCode + " не найдена."));
        if (currency == null) {
            throw new IllegalArgumentException("Валюта с кодом " + isoNumericCode + " не найдена.");
        }
        return amount.multiply(currency.getExchangeRate());
    }

    /**
     * Обновление курса валюты.
     *
     * @param isoNumericCode  числовой код валюты.
     * @param newExchangeRate новый курс обмена.
     * @return обновленный объект валюты.
     */
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

    /**
     * Удалить валюту по ID.
     *
     * @param id идентификатор валюты.
     */
    @Transactional
    public void deleteCurrency(Long id) {
        currencyRepository.deleteById(id);
    }

    /**
     * Конвертация сущности в DTO.
     */
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
