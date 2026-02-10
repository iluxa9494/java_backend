package com.skillbox.cryptobot.bot;

import com.skillbox.cryptobot.bot.command.*;
import com.skillbox.cryptobot.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CommandHandler extends TelegramLongPollingCommandBot {

    private final SubscriptionService subscriptionService;
    private final String botToken;
    private final String botUsername;
    private final SubscribeCommand subscribeCommand;
    private final UnsubscribeCommand unsubscribeCommand;
    private final GetSubscriptionCommand getSubscriptionCommand;
    private final GetPriceCommand getPriceCommand;

    @Autowired
    public CommandHandler(StartCommand startCommand, SubscribeCommand subscribeCommand,
                          UnsubscribeCommand unsubscribeCommand, GetSubscriptionCommand getSubscriptionCommand,
                          GetPriceCommand getPriceCommand, SubscriptionService subscriptionService) {
        super();
        this.botToken = System.getenv("BOT_TOKEN");
        this.botUsername = System.getenv("BOT_NAME");

        this.subscribeCommand = subscribeCommand;
        this.unsubscribeCommand = unsubscribeCommand;
        this.getSubscriptionCommand = getSubscriptionCommand;
        this.getPriceCommand = getPriceCommand;
        this.subscriptionService = subscriptionService;

        register(startCommand);
        register(subscribeCommand);
        register(unsubscribeCommand);
        register(getSubscriptionCommand);
        register(getPriceCommand);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        log.info("Получено сообщение: {}", updates);
        updates.forEach(update -> {
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
            } else {
                processNonCommandUpdate(update);
            }
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            if ("subscribe".equals(callbackData)) {
                subscriptionService.subscribe(chatId, BigDecimal.valueOf(50000));
                sendTextMessage(chatId, "✅ Вы подписаны на уведомления о BTC!");
            } else if ("get_price".equals(callbackData)) {
                sendTextMessage(chatId, "💰 Текущая цена BTC: 87,500 USD");
            }
        }
    }

    private void handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        log.info("Получен Callback Query: {} от пользователя {}", callbackData, chatId);

        try {
            switch (callbackData) {
                case "subscribe":
                    subscribeCommand.handleSubscription(chatId);
                    break;
                case "unsubscribe":
                    unsubscribeCommand.handleUnsubscription(chatId);
                    break;
                case "get_price":
                    getPriceCommand.handlePriceRequest(this, chatId);
                    break;
                case "get_subscription":
                    getSubscriptionCommand.handleSubscriptionRequest(chatId);
                    break;
                default:
                    sendTextMessage(chatId, "❌ Неизвестная команда.");
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке Callback Query {}: {}", callbackData, e.getMessage(), e);
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        try {
            execute(new SendMessage(chatId.toString(), text));
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения пользователю {}: {}", chatId, e.getMessage(), e);
        }
    }
}
