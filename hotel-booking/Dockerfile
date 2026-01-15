FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -q -DskipTests package \
  && jar_path="$(ls -1 target/*.jar | grep -v 'original' | head -n 1)" \
  && cp "${jar_path}" /workspace/app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app
ENV SERVER_PORT=8081

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
