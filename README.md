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

Маршруты Nginx (важно для health):
- `/vk-insight/health` -> `http://127.0.0.1:8007/health`

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
DB_HOST=postgres-apps
DB_PORT=5432
DB_NAME=cryptobot
DB_USER=cryptobot
DB_PASSWORD=cryptobot_password_123
```

Аналогично есть `.env` файлы в:
- `currency_exchange/.env`
- `hotel-booking/.env`
- `searchengine/.env`
- `vk_insight/.env`
- `tariff calculator/.env`
---

### hotel-booking: обязательные env и приоритеты

Обязательные переменные для PostgreSQL:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

MongoDB (приоритеты):
1. Если задан `SPRING_DATA_MONGODB_URI`, он имеет приоритет.
2. Иначе используются `SPRING_DATA_MONGODB_HOST` + `SPRING_DATA_MONGODB_PORT` + `SPRING_DATA_MONGODB_DATABASE`.

Важно:
- Если `SPRING_DATASOURCE_URL` пустой или содержит `${...}`, запуск будет остановлен с ошибкой (защита от неинициализированных env).
- Имя MongoDB БД не должно содержать `/`, `.`, пробелы, `'`, `"` или `$`.

### Как дебажить env и старт hotel-booking (на VPS)

Проверить, что `.env` смонтирован и читается:
```bash
docker exec -it java_backend ls -l /config/hotel-booking/.env
docker exec -it java_backend sed -n '1,120p' /config/hotel-booking/.env
```

Проверить реальные env у процесса Java:
```bash
docker exec -it java_backend bash -lc 'pid=$(pgrep -f "hotel-booking.jar" | head -n1); tr "\0" "\n" </proc/$pid/environ | rg "SPRING_DATASOURCE|SPRING_DATA_MONGODB"'
```

Логи и health-check:
```bash
docker logs -f java_backend
curl -fsS http://127.0.0.1:8002/actuator/health
```

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
- Для БД используем сервисные имена, без хардкода контейнеров:
```bash
docker compose -p java-backend exec mysql mysql -uroot -p
```

---
## Важно про init SQL
- Файлы в `docker-entrypoint-initdb.d` выполняются только при первом создании volume контейнера БД.
- Если volume уже существует, init-скрипты больше не применяются; используйте bootstrap-скрипты для идемпотентного создания БД/ролей.

---
## Миграции (hotel-booking)
Flyway миграции лежат в `hotel-booking/src/main/resources/db/migration`.
Для локального запуска через compose:
```bash
docker compose up --build
docker compose logs -f java_backend
```
Для чистого старта БД:
```bash
docker compose down -v
docker compose up --build
```

---
## Диагностика vk-insight (с хоста)
Проверки выполняем с VPS, без `exec` внутрь контейнеров.

```bash
curl -fsS http://127.0.0.1:8007/health
curl -i http://127.0.0.1:8007/
```

---
**Автор:** Ilia Murashkin
