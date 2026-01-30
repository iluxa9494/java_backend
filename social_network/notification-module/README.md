# MS-NOTIFICATION SERVICE

Микросервис для управления уведомлениями (notifications) в рамках
командного проекта ** JavaPro Team 58 (Skillbox) **. 
### Стек: 
- Java 21,
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring Kafka
- PostgreSQL, 
- Liquibase, 
- Maven

---

## Быстрый старт

### 1. Установка зависимостей Необходимо установить: 
- Java 21 (проверка: \`java -version\`) 
- Maven 3.9.10. (проверка: \`mvn -v\`) 
- Docker Desktop или Colima (для запуска PostgreSQL и Kafka)

### 2. Клонирование репозитория
```bash 
git clone
git@gitlab.skillbox.ru:javapro_team58/ms-notification.git cd
ms-notification
``` 

### 3. Запуск инфраструктуры (Postgres + Kafka)
```bash 
docker compose -f docker-compose.dev.yml up -d
```

#### Проверка контейнеров:
```bash 
docker ps
```

Ожидаемые сервисы: • ms-notification-postgres (порт 5432) •
ms-notification-kafka (порт 9092)

### 4. Настройка переменных окружения

Файл application.yml содержит значения по умолчанию:
```bash 
spring: datasource: url:
jdbc:postgresql://localhost:5432/notification_db username: notif
password: notif
```

### 5. Применение миграций

#### При запуске приложения автоматически применяются миграции Liquibase.
Создаются таблицы: 
- notification_settings
- notifications

Проверка в базе:

```bash 
docker exec -it ms-notification-postgres \\ psql -U notif -d
notification_db -c \"\\dt\"
```

### 6. Запуск приложения
```bash 
./mvnw clean spring-boot:run
```

или, если wrapper отсутствует:
```bash 
mvn clean spring-boot:run
```

Приложение доступно на порту 8083.

⸻

#### Основные DTO: 
-  NotificationDto - отдельное уведомление
-  SettingsDto - настройки уведомлений пользователя

⸻

### Миграции Liquibase

Все миграции расположены в каталоге src/main/resources/db/changelog/:
- 001-init-notification-settings.yaml
- 002-init-notifications.yaml •
- db.changelog-master.yaml (главный файл, который подключает все
изменения)

⸻

### Команды для разработки

#### Сборка проекта:

mvn clean package

#### Запуск тестов:

mvn test

#### Пересоздание инфраструктуры:

docker compose -f docker-compose.dev.yml down -v docker compose -f
docker-compose.dev.yml up -d
