# Dockerfile
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный JAR-файл в контейнер
COPY target/SearchEngine-1.0-SNAPSHOT.jar app.jar

# Копируем SQL скрипт для инициализации базы данных (если используется PostgreSQL)
COPY db/init.sql /docker-entrypoint-initdb.d/

# Открываем порт для приложения
EXPOSE 8080

# Устанавливаем точку входа
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]