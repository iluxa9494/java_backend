# Dockerfile
FROM gradle:8.3-jdk17 AS app

WORKDIR /app

# 1) Копируем только то, что нужно для кеширования зависимостей
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 2) Прогреваем зависимости (ускоряет последующие сборки образа)
RUN ./gradlew --no-daemon -v

# 3) Копируем исходники
COPY src ./src

EXPOSE 8080

# Важно: не делаем jar, а запускаем bootRun
# Порт внутри контейнера: 8080 (по умолчанию у Spring Boot)
CMD ["./gradlew", "--no-daemon", "bootRun"]