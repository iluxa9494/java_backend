FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/social-network-dialog-0.0.1-SNAPSHOT.jar dialog.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "dialog.jar"]