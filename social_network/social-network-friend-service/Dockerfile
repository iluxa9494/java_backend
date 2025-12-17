FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

# Кеш зависимостей: сначала только pom, потом go-offline
COPY pom.xml /app/pom.xml
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests dependency:go-offline

# Исходники
COPY src /app/src

# Порт задаём аргументом (для удобства)
ARG APP_PORT=8080
EXPOSE ${APP_PORT}

# Запуск без jar: прямо через Maven
ENV MAVEN_OPTS="-XX:MaxRAMPercentage=75.0"
CMD ["mvn", "-q", "-DskipTests", "spring-boot:run"]