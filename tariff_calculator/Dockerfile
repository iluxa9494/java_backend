FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

# Чтобы зависимости кешировались
COPY pom.xml ./
COPY app/pom.xml app/pom.xml
COPY domain/pom.xml domain/pom.xml
COPY useCase/pom.xml useCase/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml
RUN mvn -q -DskipTests dependency:go-offline

# Остальной код
COPY . .

RUN mvn -q -pl app -am -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

RUN apt-get update \
  && apt-get install -y --no-install-recommends curl \
  && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/app/target/app-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
