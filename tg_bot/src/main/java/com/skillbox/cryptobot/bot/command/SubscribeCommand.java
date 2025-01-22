package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscriber;
import com.skillbox.cryptobot.repository.SubscriberRepository;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.Command;

import java.util.Optional;
import java.util.UUID;

public class SubscribeCommand implements CommandExecutor {

    private final SubscriberRepository subscriberRepository;

    public SubscribeCommand(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public void execute(Message message) {
        String[] args = message.getText().split(" ");
        if (args.length != 2 || !args[1].matches("\\d+(\\.\\d+)?")) {
            sendMessage(message.getChatId(), "Неверный формат команды. Используйте /subscribe [цена].");
            return;
        }

        double price = Double.parseDouble(args[1]);
        long telegramId = message.getFrom().getId();

        Optional<Subscriber> existingSubscriber = subscriberRepository.findByTelegramId(telegramId);
        Subscriber subscriber = existingSubscriber.orElseGet(() -> {
            Subscriber newSubscriber = new Subscriber();
            newSubscriber.setUuid(UUID.randomUUID());
            newSubscriber.setTelegramId(telegramId);
            return newSubscriber;
        });

        subscriber.setSubscriptionPrice(price);
        subscriberRepository.save(subscriber);

        sendMessage(message.getChatId(), "Новая подписка создана на стоимость " + price + " USD.");
    }
}
