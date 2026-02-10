FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY install-local-deps.sh ./
COPY apache_lucene_morphology/ ./apache_lucene_morphology/
COPY local-repo/ ./local-repo/

RUN chmod +x ./install-local-deps.sh && ./install-local-deps.sh
RUN mvn -q -DskipTests dependency:go-offline

COPY src/ ./src/
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
