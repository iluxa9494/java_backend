package ru.skillbox.currency.exchange.scheduler;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import ru.skillbox.currency.exchange.service.CurrencyUpdateService;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestPropertySource(properties = "scheduler.cron.expression=* * * * * *")
public class CurrencyUpdateSchedulerTest {

    @SpyBean
    private CurrencyUpdateService currencyUpdateService;

    @Autowired
    private CurrencyUpdateScheduler currencyUpdateScheduler;

    @Test
    public void testScheduledCurrencyUpdateIsCalled() {
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        Mockito.verify(currencyUpdateService, Mockito.atLeast(1)).updateCurrencies()
                );
    }
}
