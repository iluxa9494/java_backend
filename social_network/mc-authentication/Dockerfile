FROM eclipse-temurin:21-jdk

WORKDIR /mc-authentication

COPY target/authentication-0.0.1-SNAPSHOT.jar authentication.jar

EXPOSE 8081

CMD ["java", "-jar", "authentication.jar"]