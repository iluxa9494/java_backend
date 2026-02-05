package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
public class GetPriceCommand extends BotCommand {

    private final CryptoCurrencyService service;

    public GetPriceCommand(CryptoCurrencyService service) {
        super("get_price", "Получить текущую цену BTC");
        this.service = service;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long chatId = chat.getId();
        log.info("Получен запрос цены BTC от пользователя {}", chatId);
        handlePriceRequest(absSender, chatId);
    }

    public void handlePriceRequest(AbsSender absSender, Long chatId) {
        log.info("Обрабатываем Callback Query: получение цены BTC для пользователя {}", chatId);

        try {
            BigDecimal price = service.getBitcoinPrice();
            sendMessage(absSender, chatId, "📈 Текущая цена BTC: " + price + " USD.");
        } catch (IOException e) {
            log.error("Ошибка при получении цены BTC: {}", e.getMessage(), e);
            sendMessage(absSender, chatId, "❌ Ошибка получения данных с Binance API.");
        }
    }

    private void sendMessage(AbsSender sender, Long chatId, String text) {
        if (sender == null) {
            log.error("Ошибка: AbsSender не может быть null при отправке сообщения.");
            return;
        }
        try {
            sender.execute(new SendMessage(chatId.toString(), text));
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения пользователю {}: {}", chatId, e.getMessage(), e);
        }
    }
}