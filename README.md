# java_backend (Monorepo Docker Compose Setup)

Этот репозиторий объединяет 6 Java (и Node.js) pet-проектов Ильи в единую инфраструктуру, запускаемую через `docker-compose.full.yml`. Все сервисы работают на отдельных портах и используют свои базы данных.

---
## ⚡️ Быстрый старт

### 1. Клонируй репозиторий (если не сделал)
```bash
git clone https://github.com/iluxa9494/java_backend.git
cd java_backend
```

### 2. Установи Docker и Docker Compose (если не установлены)
```bash
sudo apt update
sudo apt install docker.io docker-compose -y
```

### 3. Запусти все сервисы
```bash
chmod +x start_all.sh stop_all.sh
./start_all.sh
```

### 4. Останови все сервисы
```bash
./stop_all.sh
```

---

## 🚀 Запускаемые проекты и порты

| Проект              | Описание                                        | Порт   |
|---------------------|-------------------------------------------------|--------|
| `currency_exchange` | Обмен валют с PostgreSQL                        | `8080` |
| `hotel-booking`     | Бронирование отелей, Kafka + Mongo + PostgreSQL | `8081` |
| `searchengine`      | Индексация сайтов, MySQL                        | `8082` |
| `tg_bot`            | Telegram-бот криптоаналитики                    | `8083` |
| `vk_insight`        | Аналитика VK, Node.js + MySQL                   | `8084` |
| `tariff calculator` | Расчёт стоимости доставки                       | `8085` |


---

## 🔐 Переменные окружения

Каждый проект использует **свой `.env` файл**. Примеры:

### `tg_bot/.env`
```
TELEGRAM_BOT_USERNAME=MUR_BTCBot
TELEGRAM_BOT_TOKEN=your_bot_token
POSTGRES_DB=cryptobot
POSTGRES_USER=bot_user
POSTGRES_PASSWORD=secure_password
```

Аналогично есть `.env` файлы в:
- `currency_exchange/.env`
- `hotel-booking/.env`
- `searchengine/.env`
- `vk_insight/.env`
- `tariff calculator/.env`
---

## 🔄 Логика
- Каждый сервис работает в своём контейнере
- Каждая БД — в отдельном контейнере (PostgreSQL, MySQL, MongoDB, Kafka)
- Все они объединены в одну виртуальную сеть через Docker Compose
- Переменные окружения из `.env` читаются и подставляются в конфиги приложений (`application.properties`, `application.yml`)

---

## 📈 Мониторинг / Логи
- Docker логи можно просматривать командой:
```bash
docker logs <имя_контейнера> -f
```
- Все контейнеры именованы: `currency_exchange`, `hotel_booking`, `searchengine`, `tg_bot`, `vk_insight`, и т.д.

---
**Автор:** Ilia Murashkin