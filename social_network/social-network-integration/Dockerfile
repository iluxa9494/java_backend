FROM openjdk:21-jdk-slim
LABEL authors="Сергей Лукьяненко"

WORKDIR /app
COPY ./target/*.jar app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]