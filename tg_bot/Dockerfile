# ===== Build stage =====
FROM gradle:8.3-jdk17 AS build
WORKDIR /app

# Для кеша зависимостей
COPY gradlew ./
COPY gradle/ ./gradle/
COPY settings.gradle build.gradle ./
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon -v

# Исходники
COPY src/ ./src/

# Собираем jar
RUN ./gradlew --no-daemon clean bootJar

# ===== Runtime stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8083

# Запуск
ENTRYPOINT ["java", "-jar", "/app/app.jar"]