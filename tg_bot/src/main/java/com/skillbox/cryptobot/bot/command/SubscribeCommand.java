package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;

@Slf4j
@Component
public class SubscribeCommand extends BotCommand {
    private final SubscriptionService subscriptionService;

    public SubscribeCommand(SubscriptionService subscriptionService) {
        super("subscribe", "Подписаться на уведомления");
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long telegramId = chat.getId();

        if (arguments.length == 0) {
            sendMessage(absSender, telegramId, "❌ Ошибка: укажите цену для подписки. Пример: `/subscribe 35000`");
            return;
        }

        try {
            BigDecimal targetPrice = new BigDecimal(arguments[0]);
            subscriptionService.subscribe(telegramId, targetPrice);
            sendMessage(absSender, telegramId, "✅ Вы подписаны на уведомления о BTC при цене ниже " + targetPrice + " USD");
        } catch (NumberFormatException e) {
            sendMessage(absSender, telegramId, "❌ Ошибка: укажите корректную цену (например, `/subscribe 35000`)");
        }
    }

    public void handleSubscription(Long chatId) {
        sendMessage(null, chatId, "✅ Ваша подписка обработана.");
    }

    private void sendMessage(AbsSender sender, Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            if (sender != null) {
                sender.execute(message);
            }
            log.info("Сообщение отправлено пользователю {}: {}", chatId, text);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения пользователю {}: {}", chatId, e.getMessage(), e);
        }
    }
}
