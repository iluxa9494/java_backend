# Hotel Booking API 🏨

## 1. Описание проекта

Сервис для управления бронированием отелей. Поддерживает CRUD-операции для отелей, комнат, пользователей, бронирований и
оценок. Встроено логирование, фильтрация, пагинация, хранение статистики в MongoDB, интеграция с Kafka и авторизация
через Spring Security.

## 2. Технологии

- **Язык:** Java 17
- **Фреймворк:** Spring Boot
- **Безопасность:** Spring Security
- **JPA / ORM:** Spring Data JPA, PostgreSQL
- **NoSQL:** MongoDB
- **Миграции:** Flyway
- **Логирование:** Logback (в файл `logs/app.log`)
- **API:** REST + Swagger
- **Тесты:** JUnit 5, Mockito, Testcontainers

## 3. Установка и запуск

### 3.1. Требования

- JDK 17+
- Maven 3.8+
- PostgreSQL 16+
- MongoDB 6+
- Docker (опционально)

### 3.2. Настройка PostgreSQL
```sql
CREATE DATABASE hotel_booking;
CREATE USER hotel_admin WITH PASSWORD 'postgres';
ALTER ROLE hotel_admin SET client_encoding TO 'utf8';
ALTER ROLE hotel_admin SET default_transaction_isolation TO 'read committed';
ALTER ROLE hotel_admin SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE hotel_booking TO hotel_admin;
```

### 3.3. Применение миграций (Flyway)

```bash
./mvnw flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/hotel_booking \
  -Dflyway.User=hotel_admin \
  -Dflyway.password=postgres \
  -Dflyway.outOfOrder=true
```

Проверка статуса:
```sql
SELECT * FROM flyway_schema_history;
```

#### Описание файлов миграций

| Файл                      | Назначение       |
|---------------------------|------------------|
| V2__add_hotel_table.sql   | Таблица hotels   |
| V3__add_room_table.sql    | Таблица rooms    |
| V4__add_user_table.sql    | Таблица users    |
| V5__add_booking_table.sql | Таблица bookings |
| V6__add_ratings_table.sql | Таблица ratings  |
| V7__add_roles_table.sql   | Таблица roles    |

### 3.4. Проверка данных PostgreSQL
```sql
SELECT * FROM users;
SELECT * FROM hotels;
SELECT * FROM rooms;
SELECT * FROM bookings;
SELECT * FROM ratings;
SELECT * FROM roles;
```

### 3.5. Настройка MongoDB

```bash
docker run -d --name mongodb -p 27017:27017 mongo
```

```js
mongosh
use hotel_booking
db.statistics.createIndex({ "eventType": 1 });
db.statistics.createIndex({ "userId": 1 });
db.statistics.createIndex({ "timestamp": -1 });
```

```bash
db.statistics.find().pretty()
```

## 4. Swagger API

- http://localhost:8080/swagger-ui/index.html

## 5. application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hotel_booking
spring.datasource.username=hotel_admin
spring.datasource.password=postgres
spring.data.mongodb.uri=mongodb://localhost:27017/hotel_booking
logging.file.name=logs/app.log
logging.level.root=INFO
```

### MongoDB env (приоритет)

- **Приоритет:** `SPRING_DATA_MONGODB_URI` (если задан) → игнорируются HOST/PORT/DATABASE.
- Если URI не задан, он собирается из:
  - `SPRING_DATA_MONGODB_HOST` (по умолчанию `mongodb`)
  - `SPRING_DATA_MONGODB_PORT` (по умолчанию `27017`)
  - `SPRING_DATA_MONGODB_DATABASE` (по умолчанию `hotel_booking`)

## 6. Базы данных

### **Используемые БД**

Проект использует **две базы данных**:

1. **PostgreSQL** – для хранения основной информации (отели, комнаты, пользователи, бронирования и роли).
2. **MongoDB** – для хранения статистики (события регистрации и бронирования).

---

### **PostgreSQL**

#### **Статус миграций и проверка данных**

```sql

---

### **MongoDB**

#### **1. Запуск MongoDB**
```sh
docker run -d --name mongodb -p 27017:27017 mongo
```

#### **2. Создание базы и коллекции**

MongoDB создаёт коллекцию `statistics` автоматически при первой вставке данных.

#### **3. Индексация (однократно)**
```sh
mongosh
use hotel_booking
db.statistics.createIndex({ "eventType": 1 });
db.statistics.createIndex({ "userId": 1 });
db.statistics.createIndex({ "timestamp": -1 });
```

#### **4. Проверка данных**
```sh
db.statistics.find().pretty()
```

---

## 7. Структура проекта

```
src/
├── main/
│   ├── java/com/example/hotel_booking/
│   │   ├── controller/      # REST-контроллеры (обработка HTTP-запросов)
│   │   ├── dto/             # Data Transfer Objects (входные/выходные модели)
│   │   ├── mapper/          # Классы MapStruct для преобразования DTO <-> Entity
│   │   ├── model/           # Сущности JPA (Entity)
│   │   ├── repository/      # Интерфейсы для доступа к БД (JPA, Mongo)
│   │   ├── service/         # Бизнес-логика приложения
│   │   ├── config/          # Конфигурационные классы (Security, Kafka, Logging)
│   │   └── HotelBookingApplication.java  # Главный класс запуска Spring Boot приложения
│   └── resources/
│       ├── application.properties        # Настройки приложения
│       └── db/migration/                 # SQL-миграции для Flyway
└── test/
    └── java/com/example/hotel_booking/
        └── service/                      # Юнит-тесты сервисного слоя (JUnit, Mockito)
```

## 8. Тестирование

```bash
mvn test
```

## 9. Логирование

- Файл: `logs/app.log`
- Уровни: INFO, DEBUG, ERROR
- Примеры:

```text
INFO  [BookingService] Creating booking for user: 1
WARN  [AuthController] Invalid login attempt
```
