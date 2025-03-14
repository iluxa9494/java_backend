# Сервис Обмена Валюты

Приложение для практического кейса "Обмен Валюты" обучающей платформы Skillbox.

## Используемые технологии

- **Spring Boot 2.7**
- **Maven 3**
- **Lombok**
- **Mapstruct**
- **Liquibase**
- **PostgreSQL**
- **JUnit 5, Spring Boot Test, Mockito** (для тестирования)
- **TestContainers** (для интеграционного тестирования с реальной БД)
- **LogCaptor** (для логирования тестов)

## Требования к окружению

### JDK 17

Проект использует синтаксис Java 17. Для локального запуска вам потребуется установленный **JDK 17**.

### Docker

Для запуска проекта вам потребуется установленный и запущенный **Docker**. Для запуска БД (PostgreSQL) выполните
команду:
```bash
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres -d postgres
```

Пользователь для подключения к контейнеру: **postgres**.

## Настройка и миграция базы данных

Перед запуском убедитесь, что база данных **currency** создана. Миграция базы выполняется с помощью **Liquibase**:
```bash
liquibase update
```

После успешного выполнения миграции база должна содержать таблицу **currency** с предзаполненными данными.
Проверить успешность миграции можно с помощью команд:
```bash
psql -U postgres -d currency -c "SELECT * FROM currency;"
```

и
```bash
psql -U postgres -d currency -c "SELECT id, filename, dateexecuted FROM databasechangelog ORDER BY dateexecuted DESC;"
```

## Запуск проекта

### В IntelliJ IDEA

Запустите **main** метод класса `ru.skillbox.currency.exchange.Application`.

### Через Maven

При запуске приложения с помощью Maven выполняется предварительное тестирование перед запуском сервиса:
```bash
mvn clean spring-boot:run
```

Если тесты проходят успешно, приложение запускается без дополнительных действий.

## API Запросы

### Создание новой записи о валюте:
```bash
curl --request POST \  
  --url http://localhost:8080/api/currency/create \  
  --header 'Content-Type: application/json' \  
  --data '{
    "name": "Доллар Готэм-Сити",
    "nominal": 3,
    "value": 32.2,
    "isoNumCode": 1337
}'
```

### Получение валюты по ID:
```bash
curl --request GET \  
  --url http://localhost:8080/api/currency/1333
```

### Конвертация валюты по числовому коду:
```bash
curl --request GET \  
  --url http://localhost:8080/api/currency/convert?value=100&numCode=840
```

## Тестирование

Проект покрыт тестами, написанными с использованием **JUnit 5**, **Spring Boot Test** и **Mockito**.

### **1. Unit-тесты для контроллеров API**

- Проверка успешного получения списка всех валют
- Проверка получения валюты по ID и по ISO-коду
- Тестирование конвертации валют
- Тестирование создания, обновления и удаления валют с проверкой корректности обработки ошибок

### **2. Unit-тесты для сервисного слоя**

- Тестирование логики конвертации валют (`convertCurrency`)
- Тестирование обновления курса валют (`updateCurrencyRate`)
- Проверка обработки несуществующих валют (например, выбрасывание исключения)

### **3. Тесты для компонента, работающего с внешним сервисом (CurrencyLoader)**

- Мокирование **RestTemplate** для проверки корректного парсинга XML-ответа
- Проверка обновления/создания записей валют в БД на основании полученных данных

### **4. Тесты для планировщика (Scheduler)**

- Проверка, что шедулер вызывает метод обновления курсов согласно заданной периодичности (с использованием **Spring Boot
  Test** и **Awaitility**)

### **5. Тестирование обработки исключений**

- Проверка, что при возникновении ошибок вызывается соответствующий **ExceptionHandler** и формируется корректное
  сообщение об ошибке

## Замечание по запуску тестов

При запуске приложения через Maven (`mvn clean spring-boot:run`) сначала выполняется **mvn verify**, где пробегают все
тесты. Если тесты проходят успешно, приложение запускается. Это гарантирует корректность работы ключевого функционала
перед запуском сервиса.

## Docker и дополнительные настройки

Для работы приложения с PostgreSQL рекомендуется использовать **Docker-контейнер**, как описано выше. Также **Liquibase
** используется для миграций базы данных, и его конфигурация находится в ресурсах проекта.

