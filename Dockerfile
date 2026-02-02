FROM maven:3.9.9-eclipse-temurin-21 AS build-java

WORKDIR /build

COPY currency_exchange/pom.xml currency_exchange/pom.xml
COPY hotel-booking/pom.xml hotel-booking/pom.xml
COPY searchengine/pom.xml searchengine/pom.xml
COPY searchengine/install-local-deps.sh searchengine/install-local-deps.sh
COPY searchengine/apache_lucene_morphology/ searchengine/apache_lucene_morphology/
COPY searchengine/local-repo/ searchengine/local-repo/
COPY tg_bot/build.gradle tg_bot/build.gradle
COPY tg_bot/settings.gradle tg_bot/settings.gradle
COPY tg_bot/gradlew tg_bot/gradlew
COPY tg_bot/gradle/ tg_bot/gradle/

COPY social_network/pom.xml social_network/pom.xml
COPY social_network/social-network-app/pom.xml social_network/social-network-app/pom.xml
COPY social_network/auth-module/pom.xml social_network/auth-module/pom.xml
COPY social_network/account-module/pom.xml social_network/account-module/pom.xml
COPY social_network/friend-module/pom.xml social_network/friend-module/pom.xml
COPY social_network/post-module/pom.xml social_network/post-module/pom.xml
COPY social_network/dialog-module/pom.xml social_network/dialog-module/pom.xml
COPY social_network/notification-module/pom.xml social_network/notification-module/pom.xml
COPY social_network/integration-module/pom.xml social_network/integration-module/pom.xml
COPY social_network/social-network-frontend/package.json social_network/social-network-frontend/package.json
COPY social_network/social-network-frontend/package-lock.json social_network/social-network-frontend/package-lock.json
COPY social_network/social-network-frontend/vue.config.js social_network/social-network-frontend/vue.config.js

RUN --mount=type=cache,target=/root/.m2 \
    set -eu; \
    chmod +x /build/searchengine/install-local-deps.sh; \
    for module in \
      currency_exchange \
      hotel-booking \
      searchengine; do \
      mvn -q -DskipTests -f "/build/${module}/pom.xml" dependency:go-offline; \
    done; \
    mvn -q -DskipTests -f "/build/social_network/pom.xml" dependency:go-offline

COPY currency_exchange currency_exchange
COPY hotel-booking hotel-booking
COPY searchengine searchengine
COPY tg_bot tg_bot
COPY social_network social_network

RUN --mount=type=cache,target=/root/.m2 \
    set -eu; \
    mkdir -p /out; \
    chmod +x /build/searchengine/install-local-deps.sh; \
    (cd /build/searchengine && ./install-local-deps.sh); \
    (cd /build/currency_exchange && mvn -q -DskipTests package); \
    (cd /build/hotel-booking && mvn -q -DskipTests package); \
    (cd /build/searchengine && mvn -q -DskipTests package); \
    (cd /build/tg_bot && chmod +x ./gradlew && ./gradlew --no-daemon clean bootJar); \
    (cd /build/social_network && mvn -q -DskipTests package); \
    \
    cp "$(ls -1 /build/currency_exchange/target/*.jar | grep -v 'original' | head -n1)" /out/currency-exchange.jar; \
    cp "$(ls -1 /build/hotel-booking/target/*.jar | grep -v 'original' | head -n1)" /out/hotel-booking.jar; \
    cp "$(ls -1 /build/searchengine/target/*.jar | grep -v 'original' | head -n1)" /out/searchengine.jar; \
    cp "$(ls -1 /build/tg_bot/build/libs/*.jar | head -n1)" /out/tg-bot.jar; \
    jar="$(ls -1 /build/social_network/social-network-app/target/*.jar | grep -vE '(-sources|-javadoc|\\.original|plain)\\.jar$' | head -n1)"; \
    test -n "${jar}"; \
    cp "${jar}" /out/social-network.jar

FROM maven:3.9.9-eclipse-temurin-17 AS build-tariff

WORKDIR /build/tariff_calculator

COPY tariff_calculator/pom.xml ./pom.xml
COPY tariff_calculator/app/pom.xml ./app/pom.xml
COPY tariff_calculator/domain/pom.xml ./domain/pom.xml
COPY tariff_calculator/web/pom.xml ./web/pom.xml
COPY tariff_calculator/useCase/pom.xml ./useCase/pom.xml
COPY tariff_calculator/persistence/pom.xml ./persistence/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    set -eu; \
    mvn -q -DskipTests dependency:go-offline

COPY tariff_calculator ./ 

RUN --mount=type=cache,target=/root/.m2 \
    set -eu; \
    mvn -q -DskipTests -pl app -am package; \
    mkdir -p /out; \
    cp "$(ls -1 /build/tariff_calculator/app/target/*.jar | head -n1)" /out/tariff-calculator.jar

FROM node:20-bullseye-slim AS build-vk

WORKDIR /vk_insight
COPY vk_insight/package*.json ./
RUN npm ci --omit=dev
COPY vk_insight ./

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends nodejs ca-certificates curl; \
    rm -rf /var/lib/apt/lists/*

COPY --from=build-java /out/*.jar /app/
COPY --from=build-tariff /out/tariff-calculator.jar /app/tariff-calculator.jar
COPY --from=build-vk /vk_insight /app/vk_insight
COPY entrypoint-java-backend.sh /entrypoint-java-backend.sh

RUN chmod +x /entrypoint-java-backend.sh

EXPOSE 8001 8002 8003 8004 8006 8007

ENTRYPOINT ["/entrypoint-java-backend.sh"]
