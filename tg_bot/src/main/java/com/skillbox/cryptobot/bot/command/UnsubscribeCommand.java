package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Service
public class UnsubscribeCommand extends BotCommand {

    private final SubscriptionService subscriptionService;

    public UnsubscribeCommand(SubscriptionService subscriptionService) {
        super("unsubscribe", "Отменить подписку на курс биткоина");
        this.subscriptionService = subscriptionService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        subscriptionService.unsubscribe(chat.getId());
        sendMessage(absSender, chat.getId(), "Подписка отменена.");
    }

    private void sendMessage(AbsSender sender, Long chatId, String text) {
        try {
            sender.execute(new org.telegram.telegrambots.meta.api.methods.send.SendMessage(chatId.toString(), text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleUnsubscription(Long chatId) {
        log.info("Обрабатываем отписку пользователя {}", chatId);
        subscriptionService.unsubscribe(chatId);
        sendMessage(null, chatId, "✅ Вы отписались от уведомлений о BTC!");
    }
}