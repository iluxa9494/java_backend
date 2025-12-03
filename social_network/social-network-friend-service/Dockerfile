FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/friend-0.0.1-SNAPSHOT.jar ms-friend.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "ms-friend.jar"]
