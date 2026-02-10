package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Service
public class StartCommand extends BotCommand {

    private final SubscriberRepository subscriberRepository;

    public StartCommand(SubscriberRepository subscriberRepository) {
        super("start", "Запуск бота и регистрация пользователя");
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long telegramId = chat.getId();
        log.info("Команда /start от пользователя: {}", telegramId);

        if (subscriberRepository.findByTelegramId(telegramId).isEmpty()) {
            Subscriber newSubscriber = new Subscriber();
            newSubscriber.setTelegramId(telegramId);
            subscriberRepository.save(newSubscriber);
            log.info("Новый пользователь зарегистрирован: {}", telegramId);
            sendMessage(absSender, telegramId, "Вы зарегистрированы!");
        } else {
            log.info("Пользователь уже зарегистрирован: {}", telegramId);
            sendMessage(absSender, telegramId, "Вы уже зарегистрированы.");
        }
    }

    private void sendMessage(AbsSender sender, Long chatId, String text) {
        log.info("Отправка сообщения пользователю {}: {}", chatId, text);
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            sender.execute(message);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения пользователю {}: {}", chatId, e.getMessage(), e);
        }
    }
}