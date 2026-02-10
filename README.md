# java_backend

Монорепозиторий backend-сервисов. На VPS сервисы запускаются внутри одного контейнера `java_backend`, а общая инфраструктура (Postgres/MySQL/Mongo/Redis/Kafka/MinIO/Zookeeper) живет в отдельных контейнерах и используется всеми сервисами.

## Что внутри
Сервисы:
- `currency_exchange`
- `hotel-booking`
- `searchengine`
- `tariff_calculator`
- `tg_bot` (long polling, без внешнего порта)
- `vk_insight`
- `social_network` (api-gateway + внутренние SN сервисы)

## Стек
- Java 17, Spring Boot
- Maven
- PostgreSQL, MySQL, MongoDB, Redis
- Kafka, MinIO
- Docker

## Требования
- JDK 17+
- Maven 3.9+
- Docker + Docker Compose

## Запуск
### Локально (проект)
```bash
# пример: сборка jar
mvn -B -DskipTests clean package
```

### VPS (рабочий сценарий)
Запуск выполняется из `infra_pet_projects`:
- infra: `/home/infra_pet_projects/java_backend/docker-compose.infra.yml`
- app: `/home/infra_pet_projects/java_backend/docker-compose.yml`

```bash
cd /home/infra_pet_projects/java_backend
docker compose -f docker-compose.infra.yml up -d
docker compose -f docker-compose.yml up -d
```

## Конфигурация и env
Для каждого сервиса используется отдельный файл `.env` в его каталоге на VPS:
- `/home/pet_projects/java_backend/hotel-booking/.env`
- `/home/pet_projects/java_backend/vk_insight/.env`
- `/home/pet_projects/java_backend/tg_bot/.env`
- `/home/pet_projects/java_backend/searchengine/.env`
- `/home/pet_projects/java_backend/tariff_calculator/.env`
- `/home/pet_projects/java_backend/currency_exchange/.env`

Важно:
- `java_backend` читает env строго из сервисных путей.
- Не создавать дубли `.env` с альтернативными именами.

## Порты (VPS)
- `8001` currency-exchange
- `8002` hotel-booking
- `8003` social-network api-gateway
- `8004` searchengine
- `8006` tariff-calculator
- `8007` vk-insight

## API / Endpoints / Swagger
У каждого сервиса свой API. Базовые проверки:
- `GET /actuator/health` для Java-сервисов
- `GET /health` для `vk_insight`

Swagger зависит от конкретного сервиса; единого Swagger для монорепозитория нет.

## БД и миграции
- Общие контейнеры БД поднимаются infra-compose.
- `hotel-booking` использует Flyway (`db/migration`).
- Init-скрипты в `docker-entrypoint-initdb.d` должны быть идемпотентны.

## Структура
- каталоги сервисов: `currency_exchange`, `hotel-booking`, `searchengine`, `tariff_calculator`, `tg_bot`, `vk_insight`, `social_network`
- `entrypoint-java-backend.sh` и `docker/entrypoint-java-backend-seq.sh` — оркестрация старта сервисов
- `Dockerfile` — образ контейнера `java_backend`

## Ограничения
- На VPS не дублируем инфраструктуру БД/Redis/Kafka на каждый сервис.
- Один `java_backend` контейнер с несколькими сервисами — целевая архитектура для экономии RAM.
- Сборка образов выполняется в CI (GHCR), VPS только делает pull/up.
