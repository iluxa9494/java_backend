package com.skillbox.cryptobot.configuration;

import com.skillbox.cryptobot.bot.CryptoBot;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
public class TelegramBotConfiguration {

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @PostConstruct
    public void checkBotConfig() {
        log.info("Инициализация Telegram Bot Configuration...");
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(CryptoBot cryptoBot) {
        log.info("Регистрация бота с username: {}", cryptoBot.getBotUsername());

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(cryptoBot);

            log.info("Telegram bot registered successfully in Long Polling mode.");
            return botsApi;
        } catch (TelegramApiException e) {
            log.error("Ошибка при инициализации Telegram бота!", e);
            return null;
        }
    }
}
