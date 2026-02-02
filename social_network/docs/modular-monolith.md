# Social Network Modular Monolith

## Overview
This repository now builds a **single Spring Boot application** (`social-network-app`) that aggregates all former microservices as library modules. Only infrastructure services (Postgres, Redis, Kafka, MongoDB, MinIO) run as separate containers.

Included modules:
- auth-module
- account-module
- friend-module
- post-module
- dialog-module
- notification-module
- integration-module

## Build
From `social_network/`:

```bash
mvn -q -DskipTests package
```

The runnable jar is created at:
- `social-network-app/target/social-network-app-*.jar`

## Run locally (jar)
```bash
java -jar social-network-app/target/social-network-app-*.jar
```

The app listens on port `8080` by default (override with `SERVER_PORT`).

## Swagger/OpenAPI smoke test
```bash
curl -i http://127.0.0.1:${SERVER_PORT:-8080}/v3/api-docs
curl -i http://127.0.0.1:${SERVER_PORT:-8080}/swagger-ui/index.html
```

## Run with Docker Compose
```bash
docker compose up --build
```

This uses `social_network/docker-compose.yml` and starts:
- `social-network` (single JVM)
- Postgres, Redis, Kafka/Zookeeper, MongoDB, MinIO

Health check: `GET /actuator/health`.
Postgres is published on host port `5433` (`5433:5432`) to avoid conflicts.

## Configuration
All settings are consolidated into **one** config file:
- `social-network-app/src/main/resources/application.yml`

### Module datasource prefixes
Each module has its own datasource settings:
- `social.auth.datasource.*`
- `social.account.datasource.*`
- `social.friend.datasource.*`
- `social.post.datasource.*`
- `social.dialog.datasource.*`
- `social.notification.datasource.*`

**Note:** the Postgres container must contain the databases:
`auth_db`, `account_db`, `friend_db`, `post_db`, `dialog_db`, `notification_db` (or override the URLs).

Liquibase auto-run is disabled in the monolith to avoid running migrations only on the primary datasource.
Auth module migrations are executed via a dedicated Liquibase bean bound to `social.auth.datasource`.
Control it with:
- `SOCIAL_AUTH_LIQUIBASE_ENABLED` (default `true`)
- `SOCIAL_AUTH_LIQUIBASE_CHANGELOG` (default `classpath:db/changelog/auth/db.changelog-master.xml`)
- `SOCIAL_AUTH_DB_SCHEMA` (default `public`)
### Shared infrastructure
- Kafka: `SPRING_KAFKA_BOOTSTRAP_SERVERS` (default `kafka:29092`)
- Redis: `SPRING_DATA_REDIS_*` (default `redis_db:6379` / password `redis`)
- MongoDB: `SPRING_DATA_MONGODB_URI` (default `mongodb://mongodb:27017/appdatabase`)
- MinIO: `SOCIAL_INTEGRATION_*` and `storage.*`

### JWT
Auth module uses:
- `app.jwt.secret`
- `app.jwt.tokenExpiration`
- `app.jwt.refreshTokenExpiration`

Defaults are wired to `SOCIAL_AUTH_JWT_*` in `application.yml`.

## Security
Each module has its own `SecurityFilterChain` scoped by path matcher, so there are no conflicts inside a single JVM:
- `/api/v1/auth/**`
- `/api/v1/account/**`
- `/api/v1/friends/**`
- `/api/v1/post/**`, `/api/v1/posts/**`
- `/api/v1/dialogs/**`, `/ws/**`
- `/api/v1/notifications/**`
- `/api/v1/storage/**`, `/api/v1/geo/**`

Public endpoints are centralized in the app module (`/actuator/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/api/v1/health/**`).

## Notes / Decisions
- Internal HTTP calls between modules were replaced with direct service calls via Spring DI.
- Service discovery (Eureka) and API gateway are **not required** for runtime.
- Only one container runs business logic; infra stays separate.
