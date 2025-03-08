# 📌 Telegram Crypto Bot

**Описание:**
Telegram-бот для отслеживания курса Bitcoin (BTC) с возможностью подписки на уведомления при достижении заданной цены.

---

## 🚀 Функционал

🔹 Получение текущего курса BTC через API Binance.
🔹 Подписка на уведомления, если цена BTC опускается ниже заданного уровня.
🔹 Проверка активной подписки пользователя.
🔹 Отмена подписки.
🔹 Логирование действий и уведомлений.

---

## 📌 Доступные команды

| Команда | Описание |
|---------|----------|
| `/start` | Регистрация пользователя и вывод инструкций |
| `/get_price` | Получение текущего курса BTC |
| `/subscribe 80000` | Подписка на уведомление, если BTC опустится ниже 80000 USD |
| `/get_subscription` | Проверка активной подписки |
| `/unsubscribe` | Отмена подписки |
| `/help` | Список команд и инструкция по работе с ботом |

---

## ⚙️ Установка и запуск

### 🔹 1. Клонирование репозитория
```bash
git clone https://github.com/your-repo/tg_bot.git
cd tg_bot
```

### 🔹 2. Установка зависимостей
```bash
./gradlew clean build
```

### 🔹 3. Запуск бота
#### ✅ Запуск локально
```bash
./gradlew bootRun
```

#### ✅ Запуск через Docker
```bash
docker-compose up --build
```

---

## 🛠 Конфигурация
Бот использует переменные окружения (или `application.yaml`):
```yaml
telegram:
  bot:
    username: "MUR_BTCBot"
    token: "YOUR_TELEGRAM_BOT_TOKEN"

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cryptobot
    username: bot_user
    password: secure_password
```

Добавьте `.env` файл для удобной настройки:
```env
TELEGRAM_BOT_USERNAME=MUR_BTCBot
TELEGRAM_BOT_TOKEN=YOUR_TELEGRAM_BOT_TOKEN
```

---

## 🏗 Технологии
✅ **Java 17**  
✅ **Spring Boot 3**  
✅ **Telegram Bots API**  
✅ **PostgreSQL**  
✅ **Docker**  
✅ **Hibernate (JPA)**  
✅ **Binance API**

---

## 📜 Лицензия
Проект распространяется под **свободной лицензией**. Вы можете использовать, модифицировать и распространять его без ограничений.

---

## 🤝 Контакты и поддержка
Если у вас есть вопросы или предложения, создавайте Issue или Pull Request в репозитории!

🚀 **Удачного использования!**