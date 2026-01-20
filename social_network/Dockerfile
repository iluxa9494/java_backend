FROM maven:3.9.9-eclipse-temurin-21 AS build

SHELL ["/bin/bash", "-c"]

WORKDIR /build

COPY api-gateway/pom.xml api-gateway/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml
COPY mc-authentication/pom.xml mc-authentication/pom.xml
COPY mc-account/pom.xml mc-account/pom.xml
COPY ms-notification/pom.xml ms-notification/pom.xml
COPY social-network-post/pom.xml social-network-post/pom.xml
COPY dialog_service/pom.xml dialog_service/pom.xml
COPY social-network-friend-service/pom.xml social-network-friend-service/pom.xml
COPY social-network-integration/pom.xml social-network-integration/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    set -euo pipefail; \
    for module in \
      api-gateway \
      eureka-server \
      mc-authentication \
      mc-account \
      ms-notification \
      social-network-post \
      dialog_service \
      social-network-friend-service \
      social-network-integration; do \
      mvn -q -DskipTests -f "/build/${module}/pom.xml" dependency:go-offline; \
    done

COPY api-gateway api-gateway
COPY eureka-server eureka-server
COPY mc-authentication mc-authentication
COPY mc-account mc-account
COPY ms-notification ms-notification
COPY social-network-post social-network-post
COPY dialog_service dialog_service
COPY social-network-friend-service social-network-friend-service
COPY social-network-integration social-network-integration

RUN --mount=type=cache,target=/root/.m2 \
    set -euo pipefail; \
    modules=( \
      "api-gateway:api-gateway" \
      "eureka-server:eureka-server" \
      "mc-authentication:mc-authentication" \
      "mc-account:mc-account" \
      "ms-notification:ms-notification" \
      "social-network-post:social-network-post" \
      "dialog_service:dialog-service" \
      "social-network-friend-service:social-network-friend-service" \
      "social-network-integration:social-network-integration" \
    ); \
    mkdir -p /out; \
    for entry in "${modules[@]}"; do \
      dir="${entry%%:*}"; \
      name="${entry##*:}"; \
      (cd "/build/${dir}" && mvn -q -DskipTests package); \
      jar="$(ls -1 "/build/${dir}/target/"*.jar | grep -vE '(-sources|-javadoc|\\.original|plain)\\.jar$' | head -n1)"; \
      if [[ -z "${jar}" ]]; then \
        jar="$(ls -1 "/build/${dir}/target/"*.jar | grep -vE '(-sources|-javadoc)\\.jar$' | head -n1)"; \
      fi; \
      test -n "${jar}"; \
      cp "${jar}" "/out/${name}.jar"; \
    done

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /out/*.jar /app/
COPY entrypoint-social-network.sh /entrypoint-social-network.sh

RUN chmod +x /entrypoint-social-network.sh

EXPOSE 8765

ENTRYPOINT ["/entrypoint-social-network.sh"]
