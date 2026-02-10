FROM maven:3.9-eclipse-temurin-17

WORKDIR /workspace

# Чтобы зависимости кешировались
COPY pom.xml ./
COPY app/pom.xml app/pom.xml
COPY domain/pom.xml domain/pom.xml
COPY useCase/pom.xml useCase/pom.xml
COPY persistence/pom.xml persistence/pom.xml
COPY web/pom.xml web/pom.xml
RUN mvn -q -DskipTests dependency:go-offline

# Остальной код (при локальной разработке будет перекрыт volume'ом, но полезно для первого build)
COPY . .

EXPOSE 8080
EXPOSE 7001

# Debug + удобные дефолты
ENV MAVEN_OPTS="-Dmaven.repo.local=/workspace/.m2 -Xms256m -Xmx1024m"
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,address=*:7001,server=y,suspend=n"

# ВАЖНО: запускаем spring-boot:run в модуле app (у тебя Main.java там)
CMD ["mvn", "-q", "-pl", "app", "-am", "-DskipTests", "spring-boot:run"]