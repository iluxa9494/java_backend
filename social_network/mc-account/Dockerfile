FROM eclipse-temurin:21-jre-alpine

VOLUME /tmp

COPY target/mc-account-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]