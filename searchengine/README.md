# 🔍 Search Engine

Простой поисковый движок на Java + Spring Boot с поддержкой многопоточной индексации, лемматизации и REST API. Поддерживается веб-интерфейс с вкладками "Dashboard", "Management", "Search".

---

## 🚀 Возможности

- Индексация одного или нескольких сайтов
- Постраничная индексация (`indexPage`)
- Лемматизация с помощью Lucene Morphology
- Поиск с учётом релевантности
- Статистика по сайтам/страницам/леммам
- Веб-интерфейс с JavaScript и загрузкой через API

---

## 🛠 Стек технологий

- **Java 17**
- **Spring Boot 2.7.1**
- **Maven**
- **MySQL**
- **Apache Lucene Morphology (rus)**
- **JUnit 5 + Mockito**
- **HTML + JS + CSS**

---

## 📆 Конфигурация `application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/search_engine
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect

indexing-settings:
  sites:
    - url: https://www.playback.ru
      name: PlayBack
```

---

## 📁 Сценарий запуска

```bash
# Клонировать проект
git clone
cd searchengine

# Создать базу данных
mysql -u root -p < create_database.sql

# Запустить проект
mvn verify

# Добавить сайты
mysql -u root -p search_engine < insert_sites.sql
```

---

## 📃 `create_database.sql`

```sql
CREATE DATABASE search_engine CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

## 📃 `insert_sites.sql`

```sql
INSERT INTO site (name, url, status, status_time) VALUES
  ('Example', 'https://example.com', 'INDEXED', NOW()),
  ('Playback', 'https://www.playback.ru', 'INDEXED', NOW()),
  ('Volochek', 'https://volochek.life', 'INDEXED', NOW()),
  ('RadioMV', 'http://radiomv.ru', 'INDEXED', NOW()),
  ('IPFRAN', 'https://ipfran.ru', 'INDEXED', NOW()),
  ('DimonVideo', 'https://dimonvideo.ru', 'INDEXED', NOW()),
  ('NikoArtGallery', 'https://nikoartgallery.com', 'INDEXED', NOW()),
  ('EtCetera', 'https://et-cetera.ru/mobile', 'INDEXED', NOW()),
  ('LutheranCathedral', 'https://www.lutherancathedral.ru', 'INDEXED', NOW()),
  ('DomBulgakova', 'https://dombulgakova.ru', 'INDEXED', NOW()),
  ('Svetlovka', 'https://www.svetlovka.ru', 'INDEXED', NOW());

```

---

## 🌐 REST API

| Method | Endpoint               | Description                   |
|--------|------------------------|-------------------------------|
| GET    | /api/statistics        | Статистика по сайтам          |
| GET    | /api/startIndexing     | Запуск индексации             |
| GET    | /api/stopIndexing      | Остановка индексации          |
| POST   | /api/indexPage?url=... | Индексация отдельной страницы |
| GET    | /api/search            | Поиск с лемматизацией         |

---

## 📊 Тесты

- Юнит-тесты: `ApiControllerTest`, `IndexingServiceTest`, `SearchServiceTest`, `LemmatizerServiceTest`, `StatisticsServiceTest`
- Запуск: `mvn test` или `mvn verify`
- При успешном прохождении приложение запускается автоматически

## 🍿 Визуализация проекта
доступно: http://34.31.199.183:8082/
Вероятно, при переходе по ссылке браузер будет ругаться о ненадежности ссылки, необходимо нажать "Перейти".
---

_Проект выполнен в рамках дипломной работы по курсу Java-разработчик._

