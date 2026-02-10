FROM gradle:8.3-jdk17 AS app

WORKDIR /app

# 1) Копируем wrapper и gradle-скрипты отдельно (для кеша)
COPY gradlew ./
COPY gradle/ ./gradle/
COPY settings.gradle build.gradle ./

# gradlew должен быть исполняемым в Linux runner
RUN chmod +x ./gradlew

# 2) Проверяем, что wrapper работает (упадёт, если нет gradle-wrapper.jar)
RUN ./gradlew --no-daemon -v

# 3) Копируем исходники
COPY src/ ./src/

EXPOSE 8080

CMD ["./gradlew", "--no-daemon", "bootRun"]