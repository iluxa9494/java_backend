package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;

@Slf4j
@Service
public class GetSubscriptionCommand extends BotCommand {

    private final SubscriptionService subscriptionService;

    public GetSubscriptionCommand(SubscriptionService subscriptionService) {
        super("get_subscription", "Посмотреть подписку");
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long telegramId = chat.getId();
        BigDecimal subscription = subscriptionService.getSubscription(telegramId);
        if (subscription == null) {
            sendMessage(absSender, telegramId, "❌ У вас нет активной подписки.");
        } else {
            sendMessage(absSender, telegramId, "✅ Ваша подписка на BTC: " + subscription + " USD");
        }
    }

    private void sendMessage(AbsSender sender, Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            sender.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSubscriptionRequest(Long chatId) {
        log.info("Обрабатываем запрос подписки пользователя {}", chatId);
        BigDecimal subscription = subscriptionService.getSubscription(chatId);
        if (subscription != null) {
            sendMessage(null, chatId, "📌 Ваша подписка активна: " + subscription + " USD.");
        } else {
            sendMessage(null, chatId, "⚠️ У вас нет активной подписки.");
        }
    }
}