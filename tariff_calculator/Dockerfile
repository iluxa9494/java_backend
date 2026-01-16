FROM eclipse-temurin:17-jre

WORKDIR /app

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=app/target/app-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
