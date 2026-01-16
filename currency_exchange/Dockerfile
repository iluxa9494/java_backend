FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Кэш зависимостей
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline || true

# Исходники
COPY src ./src

RUN mvn -q -DskipTests package \
  && set -e; \
  jar_path="$(ls -1 target/*.jar | grep -v 'original' | head -n 1)"; \
  if [ -z "${jar_path}" ]; then \
    echo "No runnable jar found in target"; \
    ls -la target; \
    exit 1; \
  fi; \
  mv "${jar_path}" /workspace/app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
