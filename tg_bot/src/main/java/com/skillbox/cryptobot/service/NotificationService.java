package com.skillbox.cryptobot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    @Qualifier("cryptoBot")
    private final AbsSender telegramBot;

    public void sendPriceAlert(Long chatId, BigDecimal currentPrice) {
        String messageText = "📢 Пора покупать, стоимость биткоина " + currentPrice + " USD";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);

        try {
            telegramBot.execute(message);
            log.info("Уведомление отправлено пользователю {}: {}", chatId, messageText);
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомления пользователю {}: ", chatId, e);
        }
    }
}