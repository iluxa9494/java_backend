# 🔍 Search Engine

📖 Простой поисковый движок на Java + Spring Boot с поддержкой многопоточной индексации, лемматизации и REST API.
Поддерживается веб-интерфейс с вкладками "Dashboard", "Management", "Search".

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
- **PostgreSQL**
- **Liquibase**
- **Apache Lucene Morphology (rus)**
- **JUnit 5 + Mockito**
- **HTML + JS + CSS**

---

## 🗖 Конфигурация `application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/search_engine
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate.dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
    open-in-view: false

  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml

indexing-settings:
  sites:
    - url: https://www.playback.ru
      name: PlayBack

server:
  port: 8082
```

---

## 📁 Сценарий запуска

### Docker (единый образ из GHCR)

```bash
# Из корня репозитория
# Параметры можно переопределить в .env (SEARCHENGINE_* переменные)
docker compose --profile searchengine pull
docker compose --profile searchengine up -d
```

Порт по умолчанию: `http://localhost:18082`

---

### Локально (без Docker)

```bash
# Клонировать проект
git clone https://github.com/your-repo/searchengine.git
cd searchengine

# Установить зависимости Morphology
chmod +x install-local-deps.sh
./install-local-deps.sh

# Создать базу данных PostgreSQL
psql -U postgres -c "CREATE DATABASE search_engine;"

# Применить миграции Liquibase (выполняется автоматически при запуске)

# Запустить проект
mvn spring-boot:run
```

---

## 📃 Liquibase миграции

Миграции расположены в директории:

```
src/main/resources/db/changelog/
```

Файл-мастер:

```
db.changelog-master.yaml
```

Liquibase автоматически применит изменения при старте приложения.

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

---

## 🎡 Визуализация проекта

Ссылка: http://34.31.199.183:8082/

_Если браузер предупреждает о ненадежности — подтвердите переход вручную._

---
