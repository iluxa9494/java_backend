# Проект: Тарифный Калькулятор

Сервис для расчёта стоимости доставки грузов на основе веса и размеров упаковок, а также координат отправки и получения.

---

## Используемые технологии

- Java 17
- Spring Boot 3
- Maven 3
- JUnit 5
- Swagger / OpenAPI

---

## Локальный запуск

### Требования

- JDK 17
- Maven или Maven Wrapper

### Через IntelliJ IDEA

1. Откройте проект в IntelliJ.
2. Дождитесь завершения индексации.
3. Запустите `Main.java`:  
   `app/src/main/java/ru/fastdelivery/Main.java`

---

### Через Docker

1. Сборка проекта:

   ```bash
   ./mvnw clean package
   ```

2. Сборка Docker-образа:

   ```bash
   docker build -t ru.fastdelivery:latest .
   ```

3. Запуск контейнера:

   ```bash
   docker run -p 8081:8080 ru.fastdelivery:latest
   ```

---

### Через JAR-файл

1. Сборка:

   ```bash
   ./mvnw clean package
   ```

2. Запуск:

   ```bash
   java -jar app/target/app-1.0-SNAPSHOT.jar
   ```

---

## Тестирование

Запуск тестов с проверкой стиля кода (Checkstyle).

**Linux/macOS:**

```bash
./mvnw clean test
```

**Windows:**

```bash
./mvnw.cmd clean test
```

 Отчёты:  
`target/site/checkstyle-aggregate.html`

---

## Swagger / OpenAPI

Интерфейс для тестирования и документации API доступен по адресу:  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Архитектура проекта

Проект разделён на Maven-модули, каждый отвечает за свою область:

### `app`
- Точка входа в приложение
- Чтение конфигурации из `application.yml`
- Конфигурация бинов

### `persistence`
- Слой работы с базой данных
- Содержит реализации интерфейсов репозиториев, использует Spring Data JPA или другие средства хранения.
- Зависит от domain, может содержать аннотации Spring и работу с EntityManager.

### `domain`
- Чистые бизнес-объекты: `Pack`, `Weight`, `Currency`, `Price`
- **Нет зависимостей от Spring и других модулей**

### `useCase`
- Бизнес-логика, использующая доменные объекты
- Зависит только от `domain`, **не зависит от Spring**

### `web`
- Контроллеры, DTO, API-интерфейс
- Зависит от: `domain`, `useCase`, Spring Boot

> **Важно:**  
> Структура и зависимости между модулями являются фиксированными и изменению не подлежат.

