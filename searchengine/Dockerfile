FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

# 1) POM + локальные зависимости morphology (как у тебя в проекте)
COPY pom.xml ./
COPY install-local-deps.sh ./
COPY apache_lucene_morphology/ ./apache_lucene_morphology/
COPY local-repo/ ./local-repo/

RUN chmod +x ./install-local-deps.sh && ./install-local-deps.sh

# 2) Прогреваем зависимости (ускоряет последующие сборки)
RUN mvn -q -DskipTests dependency:go-offline

# 3) Исходники
COPY src/ ./src/

EXPOSE 8082

# Не стартуем из jar — стартуем как ты просил
CMD ["mvn", "-q", "-DskipTests", "spring-boot:run"]