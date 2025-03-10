# Сервис Обмен валюты
Приложение для практического кейса "Обмен Валюты" обучающей платформы [Skillbox](https://skillbox.ru)

## Используемые технологии

- Spring Boot 2.7
- Maven 3
- Lombok
- Mapstruct
- Liquibase
- PostgreSQL

## Требования

### JDK 17

Проект использует синтаксис Java 17. Для локального запуска вам потребуется
установленный JDK 17.

### Docker
Для запуска проекта вам потребуется установленный и запущенный Docker.
Для запуска БД (PostgreSQL) необходимо запустить соответствующий сервис в Docker.

### Подключение к интернету

Подключение к интернету необходимо для получения курсов валют.

## Полезные команды

### Запуск контейнера с базой данных

```bash
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres -d postgres
```

Пользователь для подключения к контейнеру: `postgres`.

### Настройка и миграция базы данных

Перед запуском убедитесь, что база данных **currency** создана. Миграция базы выполняется с помощью Liquibase.

```bash
liquibase update
```

После успешного выполнения миграции, база данных должна содержать таблицу **currency** и предзаполненные данные.

Проверить, что миграция прошла успешно, можно с помощью команд:

```bash
psql -U postgres -d currency -c "SELECT * FROM currency;"
```

Пример ожидаемого вывода:

```bash
 id | currency_from | currency_to |  rate   |         created_at         
----+---------------+-------------+---------+----------------------------
  1 | USD           | RUB         | 93.5224 | 2025-03-10 16:45:54.654608
  2 | EUR           | RUB         | 99.5534 | 2025-03-10 16:45:54.654608
  3 | CNY           | RUB         | 12.7660 | 2025-03-10 16:45:54.654608
(3 rows)
```

Проверка применённых миграций:

```bash
psql -U postgres -d currency -c "SELECT id, filename, dateexecuted FROM databasechangelog ORDER BY dateexecuted DESC;"
```

Пример ожидаемого вывода:

```bash
       id        |                      filename                       |        dateexecuted        
-----------------+-----------------------------------------------------+----------------------------
 insert_currency | src/main/resources/db/changelog/insert-currency.xml | 2025-03-10 16:45:54.776889
 1               | src/main/resources/db/changelog/changelog_init.xml  | 2025-03-10 16:43:57.31159
(2 rows)
```

### Запуск проекта в IntelliJ IDEA

Запустите `main` метод класса `Application`.

### API Запросы

#### Создание новой записи о валюте

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

#### Получение валюты по ID

```bash
curl --request GET \
  --url http://localhost:8080/api/currency/1333
```

#### Конвертация валюты по числовому коду

```bash
curl --request GET \
--url http://localhost:8080/api/currency/convert?value=100&numCode=840
```

