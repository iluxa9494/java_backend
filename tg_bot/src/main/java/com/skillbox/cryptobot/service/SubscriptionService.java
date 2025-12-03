package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriberRepository subscriberRepository;

    /**
     * Подписывает пользователя на курс BTC
     */
    @Transactional
    public void subscribe(Long telegramId, BigDecimal price) {
        log.info("Начинаем подписку пользователя {} на цену BTC: {} USD", telegramId, price);
        long startTime = System.currentTimeMillis();

        try {
            Optional<Subscriber> existingSubscriber = subscriberRepository.findByTelegramId(telegramId);
            Subscriber subscriber = existingSubscriber.orElseGet(() -> {
                Subscriber newSubscriber = new Subscriber();
                newSubscriber.setTelegramId(telegramId);
                return newSubscriber;
            });

            subscriber.setTargetPrice(price);
            subscriberRepository.save(subscriber);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Подписка пользователя {} на {} USD успешно сохранена за {} мс", telegramId, price, duration);

        } catch (Exception e) {
            log.error("Ошибка при подписке пользователя {}: {}", telegramId, e.getMessage(), e);
        }
    }

    /**
     * Отписывает пользователя (сбрасывает targetPrice в NULL)
     */
    @Transactional
    public void unsubscribe(Long telegramId) {
        log.info("Начинаем отписку пользователя {}", telegramId);
        long startTime = System.currentTimeMillis();

        try {
            subscriberRepository.unsubscribe(telegramId);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Пользователь {} успешно отписан за {} мс", telegramId, duration);

        } catch (Exception e) {
            log.error("Ошибка при отписке пользователя {}: {}", telegramId, e.getMessage(), e);
        }
    }

    /**
     * Получает подписку пользователя (цена BTC, на которую он подписан)
     */
    public BigDecimal getSubscription(Long telegramId) {
        log.info("Проверяем подписку пользователя {}", telegramId);
        long startTime = System.currentTimeMillis();

        try {
            BigDecimal subscription = subscriberRepository.findByTelegramId(telegramId)
                    .map(Subscriber::getTargetPrice)
                    .orElse(null);

            long duration = System.currentTimeMillis() - startTime;
            if (subscription != null) {
                log.info("Подписка пользователя {} найдена: {} USD (время запроса: {} мс)", telegramId, subscription, duration);
            } else {
                log.warn("У пользователя {} нет активной подписки (время запроса: {} мс)", telegramId, duration);
            }

            return subscription;

        } catch (Exception e) {
            log.error("Ошибка при получении подписки пользователя {}: {}", telegramId, e.getMessage(), e);
            return null;
        }
    }
}
