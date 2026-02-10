FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app

COPY pom.xml /app/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

COPY src /app/src
EXPOSE 8085
CMD ["mvn","-q","-DskipTests","spring-boot:run"]