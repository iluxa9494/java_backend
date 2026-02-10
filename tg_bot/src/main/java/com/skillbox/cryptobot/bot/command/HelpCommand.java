package com.skillbox.cryptobot.bot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class HelpCommand extends BotCommand {

    public HelpCommand() {
        super("help", "Справка по боту");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long chatId = chat.getId();

        String helpMessage = """
                🤖 *Помощь по боту*
                
                Бот предназначен для отслеживания курса BTC и уведомлений при достижении заданной стоимости.
                
                📌 *Доступные команды:*
                ✅ `/start` – Запуск бота и регистрация пользователя
                ✅ `/get_price` – Получить текущий курс BTC
                ✅ `/subscribe 80000` – Подписаться на уведомления, если цена BTC упадет ниже 80000 USD
                ✅ `/get_subscription` – Проверить вашу текущую подписку
                ✅ `/unsubscribe` – Отменить подписку
                ✅ `/help` – Показать это сообщение
                
                🔹 _Как использовать:_
                - Отправьте `/start`, чтобы зарегистрироваться в боте.
                - Используйте `/subscribe 75000`, и бот уведомит вас, когда цена BTC упадет ниже 75 000 USD.
                - Чтобы проверить текущую подписку, отправьте `/get_subscription`.
                - Если подписка больше не нужна, отмените её с помощью `/unsubscribe`.
                
                🚀 Используйте команды для взаимодействия с ботом!
                """;

        sendMessage(absSender, chatId, helpMessage);
    }

    private void sendMessage(AbsSender sender, Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.enableMarkdown(true);

        try {
            sender.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
