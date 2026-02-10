# 📌 Post Service (social-network-post)

Микросервис командного проекта **Social Network**: посты, древовидные комментарии и реакции с подсчётом агрегатов.  
Стек: **Java 21, Spring Boot 3.5.5, PostgreSQL, Liquibase, Kafka, OpenAPI/Swagger**.

---

## ⚙️ Технологии
- Spring Web, Spring Data JPA (Hibernate), Validation
- Spring for Apache Kafka (producer/listener), Kafka Admin & Health
- Spring Security (базовая dev-настройка через `X-User-Id`)
- Liquibase (миграции БД), PostgreSQL
- Actuator (health: DB/Kafka), OpenAPI 3 / Swagger UI
- Testcontainers, JUnit 5, Maven

---

## 🗄️ Схема данных (кратко)
- `post` — посты
- `comment` — комментарии (поддержка parent_id)
- `reaction` — реакции пользователя к POST/COMMENT (уникальность: entity_type+entity_id+user_id)
- `reaction_count` — агрегаты по реакциям (entity_type+entity_id+reaction_type)

---

## 🚀 Быстрый старт локально

### 1) Инфраструктура
```bash
docker compose up -d postgres kafka akhq