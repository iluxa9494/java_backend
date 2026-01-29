FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
ENV SERVER_PORT=8002

COPY --from=build /workspace/target/*.jar /app/
RUN set -e; \
  jar_path="$(ls -1 /app/*.jar | grep -v 'original' | head -n 1)"; \
  if [ -z "${jar_path}" ]; then \
    echo "No runnable jar found in /app"; \
    ls -la /app; \
    exit 1; \
  fi; \
  mv "${jar_path}" /app/app.jar; \
  find /app -maxdepth 1 -name "*.jar" ! -name "app.jar" -delete

EXPOSE 8002
CMD ["java","-jar","/app/app.jar"]
