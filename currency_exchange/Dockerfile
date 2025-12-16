FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# Кэш зависимостей
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline || true

# Исходники
COPY src ./src

EXPOSE 8080

# Можно оставить DEBUG для liquibase, но лучше через env при необходимости
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.arguments=--logging.level.liquibase=INFO"]