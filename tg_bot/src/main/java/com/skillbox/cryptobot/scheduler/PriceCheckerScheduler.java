package com.skillbox.cryptobot.scheduler;

import com.skillbox.cryptobot.client.BinanceClient;
import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import com.skillbox.cryptobot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCheckerScheduler {

    private static final int MIN_NOTIFICATION_INTERVAL_MINUTES = 8;
    private final BinanceClient binanceClient;
    private final SubscriberRepository subscriberRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 120000)
    public void checkBitcoinPrice() {
        try {
            BigDecimal currentPrice = binanceClient.getBitcoinPrice();
            log.info("Текущий курс BTC: {} USD", currentPrice);

            List<Subscriber> subscribers = subscriberRepository.findAll();

            List<Subscriber> subscribersToNotify = subscribers.stream()
                    .filter(subscriber -> subscriber.getTargetPrice() != null)
                    .filter(subscriber -> subscriber.getTargetPrice().compareTo(currentPrice) > 0)
                    .filter(subscriber -> shouldNotify(subscriber))
                    .toList();

            if (subscribersToNotify.isEmpty()) {
                log.info("Нет пользователей для уведомления.");
                return;
            }

            log.info("Отправляем уведомления {} пользователям.", subscribersToNotify.size());
            subscribersToNotify.forEach(subscriber -> {
                notificationService.sendPriceAlert(subscriber.getTelegramId(), currentPrice);
                subscriber.setLastNotificationTime(LocalDateTime.now());
                subscriberRepository.save(subscriber);
            });

        } catch (Exception e) {
            log.error("Ошибка при проверке курса BTC: ", e);
        }
    }

    private boolean shouldNotify(Subscriber subscriber) {
        if (subscriber.getLastNotificationTime() == null) {
            return true;
        }
        return subscriber.getLastNotificationTime().until(LocalDateTime.now(), ChronoUnit.MINUTES) >= MIN_NOTIFICATION_INTERVAL_MINUTES;
    }
}