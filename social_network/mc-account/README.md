
## 📱 MC-ACCOUNT — Микросервис управления аккаунтами

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-brightgreen?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-✔-316192?logo=postgresql)
![Kafka](https://img.shields.io/badge/Kafka-✔-black?logo=apache-kafka)
![Docker](https://img.shields.io/badge/Docker-✔-blue?logo=docker)

Микросервис `mc-account` отвечает за управление данными пользователей:
регистрацию, профиль, обновление информации и интеграцию с другими сервисами через Kafka и API Gateway.

---

## 🚀 Начало работы

Чтобы начать работу с этим проектом:

1. Склонируйте репозиторий
2. Убедитесь, что на вашем компьютере установлены:
   - Java 21
   - Maven
   - Docker
3. Запустите сборку и контейнер

---

## 🔧 Функциональность

- [x] Реализован основной функционал аккаунта
- [x] Настроена интеграция с Eureka
- [x] Поддержка Kafka (NewUserRegisteredEvent)
- [ ] Добавить unit-тесты (в процессе)

---

### Настройка репозитория

```bash
git remote add origin https://gitlab.skillbox.ru/javapro_team58/mc-account.git
git branch -M main
git push -uf origin main

---

## 🤝 Сотрудничество

✅ Приглашены участники команды

✅ Настроен доступ к GitLab

🔄 Открыть первый Merge Request

🔄 Включить утверждение MR

🔄 Автоматическое развертывание при успешном CI

---

## ⚙️ Тестирование и развертывание

Используем GitLab CI/CD для автоматической сборки и развёртывания:

✅ Настроен .gitlab-ci.yml

✅ Образ собирается в Docker Hub

✅ Запуск через docker compose

🔄 Подключить SAST (анализ безопасности)

🔄 Настроить автосборку при пуше

---

## 💡 Описание

mc-account — ключевой микросервис системы, отвечающий за:

📝 Регистрацию и хранение данных пользователей

🔄 Обработку событий из Kafka (NewUserRegisteredEvent)

🌐 Взаимодействие с API Gateway через REST API

🔐 Интеграцию с mc-authentication по JWT

Сервис работает на Spring Boot 3.5.6, использует PostgreSQL для хранения данных, 
Redis для кэширования, и зарегистрирован в Eureka Server.

---

## 🛠 Технологии

Категория	            Технологии

Язык	                Java 21
Фреймворк	            Spring Boot 3.5.6
База данных	            PostgreSQL
Кэширование	            Redis
Сообщения	            Apache Kafka
Контейнеризация	        Docker
REST API	            Spring Web MVC
Авторизация	            JWT
ORM	                    Spring Data JPA
Документация	       OpenAPI 3 / Swagger UI

---

## 📦 Установка

1. Клонируйте репозиторий
bash
git clone https://gitlab.skillbox.ru/javapro_team58/mc-account.git
cd mc-account
2. Соберите JAR
bash
./mvnw clean package -DskipTests
3. Соберите Docker-образ
bash
docker build -t vav396/mc-account:latest .
4. Запустите контейнер
bash
docker run -d \
  --name mc-account \
  --network infrastructure_social-network \
  -p 8088:8088 \
  -e EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE=http://ms-eureka:8761/eureka/ \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SERVER_PORT=8088 \
  vav396/mc-account:latest
  
 ---
 
## 🔌 API Endpoints

Доступны через шлюз:
👉 http://79.174.81.229:8765/api/account/api/v1/account/me

Метод	Endpoint	                Описание

GET	    /api/v1/account/me	        Получить данные текущего пользователя
PUT	    /api/v1/account/me	        Обновить профиль
POST	/api/v1/account/register	Зарегистрировать нового пользователя

🔐 Все запросы требуют JWT-токен в заголовке Authorization

---

## 🔄 Интеграция с Kafka

Слушает топик: user-topic, группа: mc-account-group

Обрабатывает события:

NewUserRegisteredEvent — новый пользователь создан

Пример обработки:

java
@EventListener
public void handle(NewUserRegisteredEvent event) {
    log.info("Новый пользователь: {}", event.getUserId());
}

---

## 📄 Документация

Swagger UI доступен по адресам:

Через шлюз: http://79.174.81.229:8765/api/account/swagger-ui.html

Локально: http://localhost:8088/swagger-ui.html

---

## 👥 Автор

Александр Веденёв

Студент Skillbox, курс "Java-разработчик"

Участник командного проекта: javapro_team58

---

## 📜 Лицензия

Проект предназначен для учебных целей. Не для коммерческого использования.

---

## 🏁 Статус проекта

✅ Проект активно развивается
✅ Основной функционал реализован
✅ Готов к интеграции и тестированию
🔄 Дополнительные функции в разработке

