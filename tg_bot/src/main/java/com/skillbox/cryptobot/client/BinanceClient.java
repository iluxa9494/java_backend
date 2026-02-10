package com.skillbox.cryptobot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BinanceClient {

    private final HttpGet httpGet;
    private final ObjectMapper mapper;
    private final CloseableHttpClient httpClient;

    public BinanceClient(@Value("${binance.api.getPrice}") String uri) {
        this.httpGet = new HttpGet(uri);
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClients.custom()
                .setConnectionTimeToLive(30, TimeUnit.SECONDS)
                .build();
    }

    public BigDecimal getBitcoinPrice() {
        int maxRetries = 3;
        int retryDelay = 3000;

        log.info("Начало запроса цены BTC с Binance API...");

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            long startTime = System.currentTimeMillis();
            try {
                HttpResponse response = httpClient.execute(httpGet);
                long duration = System.currentTimeMillis() - startTime;

                int statusCode = response.getStatusLine().getStatusCode();
                log.info("Binance API ответил: HTTP {} за {} мс (попытка {}/{})", statusCode, duration, attempt, maxRetries);

                if (statusCode == 429) {
                    log.warn("Превышен лимит запросов Binance API (429). Ждем {} сек перед повтором...", retryDelay / 1000);
                    Thread.sleep(retryDelay);
                    continue;
                }

                if (statusCode == 500) {
                    log.error("Ошибка сервера Binance API (500). Ждем {} сек перед повтором...", retryDelay / 1000);
                    Thread.sleep(retryDelay);
                    continue;
                }

                if (statusCode != 200) {
                    log.error("Ошибка Binance API: HTTP {}", statusCode);
                    throw new IOException("Ошибка Binance API: HTTP " + statusCode);
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonNode jsonNode = mapper.readTree(jsonResponse);
                BigDecimal price = new BigDecimal(jsonNode.path("price").asText());

                log.info("Текущая цена BTC: {} USD", price);
                return price;

            } catch (IOException | InterruptedException e) {
                long duration = System.currentTimeMillis() - startTime;
                log.error("Ошибка при запросе цены BTC (попытка {}/{} за {} мс): ", attempt, maxRetries, duration, e);
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }

        log.error("Все попытки запроса цены BTC завершились неудачно.");
        return BigDecimal.ZERO;
    }
}
