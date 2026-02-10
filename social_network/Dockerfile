FROM maven:3.9.9-eclipse-temurin-21 AS build

SHELL ["/bin/bash", "-c"]

WORKDIR /build

COPY pom.xml pom.xml
COPY social-network-app/pom.xml social-network-app/pom.xml
COPY auth-module/pom.xml auth-module/pom.xml
COPY account-module/pom.xml account-module/pom.xml
COPY friend-module/pom.xml friend-module/pom.xml
COPY post-module/pom.xml post-module/pom.xml
COPY dialog-module/pom.xml dialog-module/pom.xml
COPY notification-module/pom.xml notification-module/pom.xml
COPY integration-module/pom.xml integration-module/pom.xml
COPY social-network-frontend/package.json social-network-frontend/package.json
COPY social-network-frontend/package-lock.json social-network-frontend/package-lock.json
COPY social-network-frontend/vue.config.js social-network-frontend/vue.config.js

RUN --mount=type=cache,target=/root/.m2 \
    set -euo pipefail; \
    mvn -q -DskipTests dependency:go-offline

COPY social-network-app social-network-app
COPY auth-module auth-module
COPY account-module account-module
COPY friend-module friend-module
COPY post-module post-module
COPY dialog-module dialog-module
COPY notification-module notification-module
COPY integration-module integration-module
COPY social-network-frontend social-network-frontend

RUN --mount=type=cache,target=/root/.m2 \
    set -euo pipefail; \
    mvn -q -DskipTests package; \
    mkdir -p /out; \
    jar="$(ls -1 /build/social-network-app/target/*.jar | grep -vE '(-sources|-javadoc|\\.original|plain)\\.jar$' | head -n1)"; \
    test -n "${jar}"; \
    cp "${jar}" /out/social-network.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /out/social-network.jar /app/social-network.jar
COPY entrypoint-social-network.sh /entrypoint-social-network.sh

RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*

RUN chmod +x /entrypoint-social-network.sh

EXPOSE 8080

ENTRYPOINT ["/entrypoint-social-network.sh"]
