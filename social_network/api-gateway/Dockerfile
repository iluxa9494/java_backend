FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway.jar
EXPOSE 8765
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]