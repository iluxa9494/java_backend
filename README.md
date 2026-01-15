# java_backend (Monorepo Docker Compose Setup)

Этот репозиторий объединяет 6 Java (и Node.js) pet-проектов Ильи в единую инфраструктуру, запускаемую через `docker-compose.yml`. Все сервисы работают на отдельных портах и используют свои базы данных.

---
##  Быстрый старт

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

### 3. Останови сервисы локально (если поднимали вручную)
```bash
docker compose -p java-backend down
```

---
## VPS запуск

Запуск всех контейнеров происходит в родительском infra-репозитории через `bootstrap.sh`.
Этот репозиторий содержит только `docker-compose.yml`, который копируется в `/home/pet_projects/java_backend`.
Для избежания конфликтов имён используем единый проект: `COMPOSE_PROJECT_NAME=java-backend` или `docker compose -p java-backend ...`.

Переменные:
- `GHCR_OWNER` — владелец образов (по умолчанию `iluxa9494`)
- `TAG` — тег образа (по умолчанию `latest`)
- `PROFILES` — список профилей (по умолчанию `social-network`)

Порты на VPS (соответствуют конфигу Nginx):
- portfolio: `127.0.0.1:8000`
- currency-exchange: `127.0.0.1:8001`
- hotel-booking: `127.0.0.1:8002`
- social-network api-gateway: `127.0.0.1:8003`
- searchengine: `127.0.0.1:8004`
- tariff-calculator: `127.0.0.1:8006`
- vk-insight: `127.0.0.1:8007`
- akhq: `127.0.0.1:8080`
- minio: `127.0.0.1:9000`
- minio-console: `127.0.0.1:9001`

---

## Запускаемые проекты и порты

| Проект              | Описание                                        | Порт |
|---------------------|-------------------------------------------------|------|
| `currency_exchange` | Обмен валют с PostgreSQL                        | `8001`  |
| `hotel-booking`     | Бронирование отелей, Kafka + Mongo + PostgreSQL | `8002`  |
| `searchengine`      | Индексация сайтов, PostgreSQL                   | `8004`  |
| `tg_bot`            | Telegram-бот криптоаналитики                    | `—`  |
| `vk_insight`        | Аналитика VK, Node.js + MySQL                   | `8007`  |
| `tariff calculator` | Расчёт стоимости доставки                       | `8006`  |


---

## Переменные окружения

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

## Логика
- Каждый сервис работает в своём контейнере
- Каждая БД — в отдельном контейнере (PostgreSQL, MySQL, MongoDB, Kafka)
- Все они объединены в одну виртуальную сеть через Docker Compose
- Переменные окружения из `.env` читаются и подставляются в конфиги приложений (`application.properties`, `application.yml`)

---

## Мониторинг / Логи
- Docker логи можно просматривать командой:
```bash
docker compose -p java-backend logs -f <service>
```
- Имена контейнеров генерирует Docker Compose, обращаемся к сервисам по имени.

---
**Автор:** Ilia Murashkin
