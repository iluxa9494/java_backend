# **Настройка базы данных**

## **Используемые БД**
Проект использует **две базы данных**:
1. **PostgreSQL** – для хранения основной информации (отели, комнаты, пользователи, бронирования и роли).
2. **MongoDB** – для хранения статистики (события регистрации и бронирования).

---

## **Настройка PostgreSQL**
### **1. Создание базы данных и пользователя**
Для работы с PostgreSQL необходимо создать базу данных и пользователя:
```sql
CREATE DATABASE hotel_booking;
CREATE USER hotel_admin WITH PASSWORD 'postgres';
ALTER ROLE hotel_admin SET client_encoding TO 'utf8';
ALTER ROLE hotel_admin SET default_transaction_isolation TO 'read committed';
ALTER ROLE hotel_admin SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE hotel_booking TO hotel_admin;
```

### **2. Настройка Flyway для миграций**
Все миграции хранятся в папке `src/main/resources/db/migration`.
Для применения миграций используйте команду:
```sh
./mvnw flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/hotel_booking -Dflyway.user=hotel_admin -Dflyway.password=postgres -Dflyway.outOfOrder=true
```
> **Важно:** необходимо указывать полные параметры в скрипте, иначе миграция может не выполниться корректно.

Проверить статус миграций:
```sql
SELECT * FROM flyway_schema_history;
```

### **3. Описание миграционных файлов**
Каждый SQL-файл отвечает за создание соответствующей таблицы:

| Файл                           | Назначение |
|--------------------------------|-----------|
| `V2__add_hotel_table.sql`      | Создание таблицы `hotels` (отели) |
| `V3__add_room_table.sql`       | Создание таблицы `rooms` (комнаты) |
| `V4__add_user_table.sql`       | Создание таблицы `users` (пользователи) |
| `V5__add_booking_table.sql`    | Создание таблицы `bookings` (бронирования) |
| `V6__add_ratings_table.sql`    | Создание таблицы `ratings` (оценки отелей) |
| `V7__add_roles_table.sql`      | Создание таблицы `roles` (роли пользователей) |

### **4. Таблицы в PostgreSQL**
После миграций в базе данных создадутся следующие таблицы:

| Таблица    | Назначение |
|------------|-----------|
| `hotels`   | Отели |
| `rooms`    | Комнаты |
| `users`    | Пользователи |
| `bookings` | Бронирования |
| `ratings`  | Оценки отелей |
| `roles`    | Роли пользователей (Spring Security) |

### **5. Проверка данных**
Примеры SQL-запросов:
```sql
-- Проверка пользователей
SELECT * FROM users;

-- Проверка отелей
SELECT * FROM hotels;

-- Проверка комнат
SELECT * FROM rooms;

-- Проверка бронирований
SELECT * FROM bookings;

-- Проверка оценок отелей
SELECT * FROM ratings;

-- Проверка ролей пользователей
SELECT * FROM roles;
```

---

## **Настройка MongoDB**
### **1. Запуск MongoDB**
Если MongoDB не установлена, её можно запустить через Docker:
```sh
docker run -d --name mongodb -p 27017:27017 mongo
```
### **2. Создание базы и коллекции**
MongoDB автоматически создаст коллекцию `statistics` при первой вставке данных.

### **3. Индексация (разово)**
```sh
mongosh
use hotel_booking
db.statistics.createIndex({ "eventType": 1 })
db.statistics.createIndex({ "userId": 1 })
db.statistics.createIndex({ "timestamp": -1 })
```

### **4. Проверка данных в MongoDB**
```sh
db.statistics.find().pretty()
```

---