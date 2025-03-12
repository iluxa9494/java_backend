package ru.skillbox.currency.exchange.mapper;

import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-12T17:17:30+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Ubuntu)"
)
@Component
public class CurrencyMapperImpl implements CurrencyMapper {

    @Override
    public CurrencyDto convertToDto(Currency currency) {
        if ( currency == null ) {
            return null;
        }

        CurrencyDto currencyDto = new CurrencyDto();

        currencyDto.setId( currency.getId() );
        currencyDto.setName( currency.getName() );
        currencyDto.setNominal( currency.getNominal() );
        currencyDto.setIsoNumCode( currency.getIsoNumCode() );
        currencyDto.setIsoCharCode( currency.getIsoCharCode() );
        currencyDto.setExchangeRate( currency.getExchangeRate() );
        currencyDto.setCreatedAt( currency.getCreatedAt() );

        return currencyDto;
    }

    @Override
    public Currency convertToEntity(CurrencyDto currencyDto) {
        if ( currencyDto == null ) {
            return null;
        }

        Currency currency = new Currency();

        currency.setId( currencyDto.getId() );
        currency.setName( currencyDto.getName() );
        currency.setNominal( currencyDto.getNominal() );
        currency.setIsoNumCode( currencyDto.getIsoNumCode() );
        currency.setIsoCharCode( currencyDto.getIsoCharCode() );
        currency.setExchangeRate( currencyDto.getExchangeRate() );
        currency.setCreatedAt( currencyDto.getCreatedAt() );

        return currency;
    }
}
