package ru.fastdelivery.integration.cbr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.fastdelivery.persistence.entity.CurrencyEntity;
import ru.fastdelivery.persistence.repository.CurrencyJpaRepository;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CbrCurrencyUpdater {
    private static final String RUB_CODE = "RUB";

    private final CurrencyJpaRepository currencyRepository;

    @Value("${cbr.enabled:true}")
    private boolean enabled;

    @Value("${cbr.url:https://www.cbr.ru/scripts/XML_daily.asp}")
    private String cbrUrl;

    @Value("${cbr.request-timeout-ms:5000}")
    private int requestTimeoutMs;

    @Scheduled(
            fixedDelayString = "${cbr.fixed-delay-ms:86400000}",
            initialDelayString = "${cbr.initial-delay-ms:10000}"
    )
    public void refreshRates() {
        if (!enabled) {
            log.info("CBR updater is disabled.");
            return;
        }

        try {
            String body = fetchCbrXml();
            List<CurrencyEntity> updated = parseRates(body);
            upsert(updated);
            log.info("CBR rates updated: {} currencies saved.", updated.size());
        } catch (Exception ex) {
            log.error("Failed to update CBR rates", ex);
        }
    }

    @Bean
    public ApplicationRunner runCbrRefreshOnStart() {
        return args -> refreshRates();
    }

    private String fetchCbrXml() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(cbrUrl))
                .timeout(java.time.Duration.ofMillis(requestTimeoutMs))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IllegalStateException("CBR request failed with status " + response.statusCode());
        }
        log.info("CBR XML fetched: {} chars", response.body().length());
        return response.body();
    }

    private List<CurrencyEntity> parseRates(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        NodeList nodes = doc.getElementsByTagName("Valute");
        List<CurrencyEntity> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String code = getText(el, "CharCode");
            String name = getText(el, "Name");
            String nominalRaw = getText(el, "Nominal");
            String valueRaw = getText(el, "Value");
            if (code == null || nominalRaw == null || valueRaw == null) {
                continue;
            }
            BigDecimal nominal = new BigDecimal(nominalRaw.replace(",", "."));
            BigDecimal value = new BigDecimal(valueRaw.replace(",", "."));
            if (nominal.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal rate = value.divide(nominal, 6, java.math.RoundingMode.HALF_UP);
            CurrencyEntity entity = CurrencyEntity.builder()
                    .code(code)
                    .name(name)
                    .rateToRub(rate)
                    .updatedAt(now)
                    .build();
            result.add(entity);
        }

        CurrencyEntity rub = CurrencyEntity.builder()
                .code(RUB_CODE)
                .name("Российский рубль")
                .rateToRub(BigDecimal.ONE)
                .updatedAt(now)
                .build();
        result.add(rub);

        return result;
    }

    private void upsert(List<CurrencyEntity> currencies) {
        currencyRepository.saveAll(currencies);
    }

    private String getText(Element el, String tag) {
        NodeList list = el.getElementsByTagName(tag);
        if (list.getLength() == 0) {
            return null;
        }
        return list.item(0).getTextContent();
    }
}
