FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/SearchEngine-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]