package ru.skillbox.socialnetwork.account.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnetwork.account.service.AccountService;
import ru.skillbox.socialnetwork.account.authentication.events.NewUserRegisteredEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountCreationEventListener {

    private final AccountService accountService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Можно @Autowired

    // Добавляем настройку для работы с Set
    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Если RoleType — enum, Jackson справится сам
    }

    /**
     * Принимаем сырое JSON-сообщение как строку
     */
    @KafkaListener(
            topics = "user-topic",
            groupId = "mc-account-group"
    )
    public void handleNewUserRegistration(String eventJson) {
        log.info("📥 Получено сырое событие из Kafka: {}", eventJson);

        try {
            // Ручное преобразование строки в объект
            NewUserRegisteredEvent event = objectMapper.readValue(eventJson, NewUserRegisteredEvent.class);

            log.info("✅ Событие распаршено: email={}, id={}", event.getEmail(), event.getId());

            accountService.createAccountFromEvent(event);
            log.info("✅ Аккаунт успешно создан для: {}", event.getEmail());

        } catch (JsonProcessingException e) {
            log.error("❌ Ошибка парсинга JSON: {}", e.getMessage());
            throw new IllegalArgumentException("Не удалось распарсить событие из Kafka", e);
        } catch (Exception e) {
            log.error("❌ Ошибка обработки события: {}", e.getMessage());
            throw e; // Повторная доставка
        }
    }

}