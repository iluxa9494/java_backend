FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/eureka-server-0.0.1-SNAPSHOT.jar eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]