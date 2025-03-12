package ru.skillbox.currency.exchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.jaxb.CurrencyJaxb;
import ru.skillbox.currency.exchange.jaxb.CurrencyListJaxb;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Optional;

@Component
public class CurrencyLoader {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyLoader.class);

    private final CurrencyRepository currencyRepository;
    private final RestTemplate restTemplate;

    public CurrencyLoader(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
        this.restTemplate = new RestTemplate();
    }

    public void updateCurrencies() {
        logger.info("Начало обновления курсов валют");
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("https://cbr.ru/scripts/XML_daily.asp", String.class);
            String xml = response.getBody();

            JAXBContext jaxbContext = JAXBContext.newInstance(CurrencyListJaxb.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            CurrencyListJaxb currencyListJaxb = (CurrencyListJaxb) unmarshaller.unmarshal(reader);

            if (currencyListJaxb != null && currencyListJaxb.getCurrencies() != null) {
                for (CurrencyJaxb currencyJaxb : currencyListJaxb.getCurrencies()) {

                    if (currencyJaxb.getIsoCharCode() == null || currencyJaxb.getValue() == null) {
                        logger.warn("Пропуск валюты из-за отсутствующих данных: {}", currencyJaxb);
                        continue;
                    }

                    BigDecimal value = currencyJaxb.getValue();

                    Optional<Currency> currencyOpt = currencyRepository.findByIsoCharCode(currencyJaxb.getIsoCharCode());
                    Currency currency;
                    if (currencyOpt.isPresent()) {
                        currency = currencyOpt.get();
                        currency.setIsoNumCode(currencyJaxb.getIsoNumCode());
                        currency.setNominal(currencyJaxb.getNominal());
                        currency.setName(currencyJaxb.getName());
                        currency.setExchangeRate(value);
                        logger.info("Обновление валюты: {}", currency.getIsoCharCode());
                    } else {
                        currency = new Currency();
                        currency.setIsoNumCode(currencyJaxb.getIsoNumCode());
                        currency.setIsoCharCode(currencyJaxb.getIsoCharCode());
                        currency.setNominal(currencyJaxb.getNominal());
                        currency.setName(currencyJaxb.getName());
                        currency.setExchangeRate(value);
                        logger.info("Создание новой валюты: {}", currency.getIsoCharCode());
                    }
                    currencyRepository.save(currency);
                }
            } else {
                logger.error("Не удалось получить список валют из ответа ЦБ РФ");
            }
        } catch (RestClientException e) {
            logger.error("Ошибка при получении данных от ЦБ РФ: {}", e.getMessage());
        } catch (JAXBException e) {
            logger.error("Ошибка при парсинге XML-данных: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при обновлении валют: {}", e.getMessage());
        }
        logger.info("Обновление курсов валют завершено");
    }
}
