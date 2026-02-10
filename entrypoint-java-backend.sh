#!/usr/bin/env bash
set -euo pipefail

log() {
  echo "[$(date +'%H:%M:%S')] $*"
}

JB_CONFIG_DIR="${JB_CONFIG_DIR:-/config}"
EUREKA_URL="${EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE:-http://eureka-server:8761/eureka/}"

JAVA_XMX="${JAVA_XMX:-256m}"
JAVA_XMS="${JAVA_XMS:-${JAVA_XMX}}"
JAVA_MAX_METASPACE="${JAVA_MAX_METASPACE:-256m}"
JAVA_GC_OPTS="${JAVA_GC_OPTS:--XX:+UseG1GC -XX:MaxGCPauseMillis=200}"
JAVA_TOOL_OPTIONS_BASE="${JAVA_TOOL_OPTIONS_BASE:-}"

if [[ -z "${JAVA_TOOL_OPTIONS:-}" ]]; then
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS_BASE} -Xms${JAVA_XMS} -Xmx${JAVA_XMX} -XX:MaxMetaspaceSize=${JAVA_MAX_METASPACE} ${JAVA_GC_OPTS}"
fi
export JAVA_TOOL_OPTIONS

add_host_aliases() {
  local aliases=(
    eureka-server
    mc-authentication
    ms-notification
    mc-account
    dialog-service
    social-network-post
    social-network-friend-service
    social-network-integration
    ms-gateway
  )

  for name in "${aliases[@]}"; do
    if ! grep -qE "[[:space:]]${name}([[:space:]]|\$)" /etc/hosts; then
      echo "127.0.0.1 ${name}" >> /etc/hosts
    fi
  done
}

read_env_file() {
  local file="$1"
  local -n out="$2"
  out=()

  if [[ -f "${file}" ]]; then
    while IFS= read -r line || [[ -n "${line}" ]]; do
      line="${line%%$'\r'}"
      [[ -z "${line}" || "${line}" == \#* ]] && continue
      out+=("${line}")
    done < "${file}"
  fi
}

wait_for_port() {
  local name="$1"
  local port="$2"
  local retries="${3:-30}"

  for _ in $(seq 1 "${retries}"); do
    if (echo >/dev/tcp/127.0.0.1/"${port}") >/dev/null 2>&1; then
      log "${name} is listening on ${port}"
      return 0
    fi
    sleep 2
  done

  log "Timeout waiting for ${name} on ${port}"
  return 1
}

pids=()
start_java() {
  local name="$1"
  local jar="$2"
  local port="$3"
  local env_file="$4"
  shift 4

  local -a env_args
  read_env_file "${env_file}" env_args
  env_args+=(
    "SERVER_PORT=${port}"
    "EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE=${EUREKA_URL}"
    "EUREKA_INSTANCE_HOSTNAME=${name}"
  )
  env_args+=("$@")

  log "Starting ${name} (port ${port})"
  env "${env_args[@]}" java -jar "${jar}" &
  pids+=("$!")
}

start_node() {
  local name="$1"
  local workdir="$2"
  local port="$3"
  local env_file="$4"
  shift 4

  local -a env_args
  read_env_file "${env_file}" env_args
  env_args+=("PORT=${port}")
  env_args+=("$@")

  log "Starting ${name} (port ${port})"
  (cd "${workdir}" && env "${env_args[@]}" node server.js) &
  pids+=("$!")
}

shutdown() {
  log "Stopping services..."
  for pid in "${pids[@]:-}"; do
    kill "${pid}" 2>/dev/null || true
  done
  wait || true
}

trap shutdown SIGTERM SIGINT

add_host_aliases

# Social Network core
start_java "eureka-server" "/app/eureka-server.jar" "8761" ""
wait_for_port "eureka-server" "8761"

start_java "mc-authentication" "/app/mc-authentication.jar" "8081" \
  "${JB_CONFIG_DIR}/social_network/mc-authentication/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/auth_db" \
  "SPRING_DATASOURCE_USERNAME=postgres" \
  "SPRING_DATASOURCE_PASSWORD=postgres" \
  "SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/appdatabase" \
  "SPRING_DATA_REDIS_HOST=redis_db" \
  "SPRING_DATA_REDIS_PORT=6379" \
  "SPRING_DATA_REDIS_PASSWORD=redis" \
  "SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092"

wait_for_port "mc-authentication" "8081"

start_java "ms-notification" "/app/ms-notification.jar" "8083" \
  "${JB_CONFIG_DIR}/social_network/ms-notification/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/notification_db" \
  "SPRING_DATASOURCE_USERNAME=postgres" \
  "SPRING_DATASOURCE_PASSWORD=postgres" \
  "SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092"

start_java "social-network-post" "/app/social-network-post.jar" "8085" \
  "${JB_CONFIG_DIR}/social_network/social-network-post/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/post_db" \
  "SPRING_DATASOURCE_USERNAME=postgres" \
  "SPRING_DATASOURCE_PASSWORD=postgres" \
  "SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092"

start_java "dialog-service" "/app/dialog-service.jar" "8082" \
  "${JB_CONFIG_DIR}/social_network/dialog_service/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/dialog_db" \
  "SPRING_DATASOURCE_USERNAME=postgres" \
  "SPRING_DATASOURCE_PASSWORD=postgres"

start_java "mc-account" "/app/mc-account.jar" "8088" "" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/account_db" \
  "SPRING_DATASOURCE_USERNAME=postgres" \
  "SPRING_DATASOURCE_PASSWORD=postgres" \
  "SPRING_DATA_REDIS_HOST=redis_db" \
  "SPRING_DATA_REDIS_PORT=6379" \
  "SPRING_DATA_REDIS_PASSWORD=redis" \
  "SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092"

start_java "social-network-friend-service" "/app/social-network-friend-service.jar" "8084" \
  "${JB_CONFIG_DIR}/social_network/social-network-friend-service/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/friend_db" \
  "SPRING_DATASOURCE_USERNAME=postgres" \
  "SPRING_DATASOURCE_PASSWORD=postgres" \
  "SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092"

start_java "social-network-integration" "/app/social-network-integration.jar" "8086" \
  "${JB_CONFIG_DIR}/social_network/social-network-integration/.env" \
  "SPRING_REDIS_HOST=redis_db" \
  "SPRING_REDIS_PORT=6379" \
  "SPRING_REDIS_PASSWORD=redis" \
  "STORAGE_S3_ENDPOINT=http://minio:9000"

start_java "ms-gateway" "/app/api-gateway.jar" "8003" "" \
  "EUREKA_INSTANCE_HOSTNAME=ms-gateway"

# Other apps
start_java "currency-exchange" "/app/currency-exchange.jar" "8001" \
  "${JB_CONFIG_DIR}/currency_exchange/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/currency" \
  "SPRING_DATASOURCE_USERNAME=currency_exchange" \
  "SPRING_DATASOURCE_PASSWORD=currency_exchange_password"

start_java "hotel-booking" "/app/hotel-booking.jar" "8002" \
  "${JB_CONFIG_DIR}/hotel-booking/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/hotel_booking" \
  "SPRING_DATASOURCE_USERNAME=hotel_admin" \
  "SPRING_DATASOURCE_PASSWORD=postgres" \
  "SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092" \
  "SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/hotel_booking" \
  "SPRING_DATA_MONGODB_DATABASE=hotel_booking"

start_java "searchengine" "/app/searchengine.jar" "8004" "" \
  "SERVER_SERVLET_CONTEXT_PATH=/search-engine" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/search_engine" \
  "SPRING_DATASOURCE_USERNAME=search_engine_user" \
  "SPRING_DATASOURCE_PASSWORD=postgres"

start_java "tariff-calculator" "/app/tariff-calculator.jar" "8006" \
  "${JB_CONFIG_DIR}/tariff_calculator/.env" \
  "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/tariff_calculator" \
  "SPRING_DATASOURCE_USERNAME=tariff_calculator" \
  "SPRING_DATASOURCE_PASSWORD=tariff_calculator_password"

start_java "tg-bot" "/app/tg-bot.jar" "8087" \
  "${JB_CONFIG_DIR}/tg_bot/.env" \
  "DB_HOST=postgres" \
  "DB_PORT=5432" \
  "DB_NAME=cryptobot" \
  "DB_USER=cryptobot" \
  "DB_PASSWORD=cryptobot_password_123"

start_node "vk-insight" "/app/vk_insight" "8007" \
  "${JB_CONFIG_DIR}/vk_insight/.env" \
  "DB_HOST=mysql" \
  "DB_PORT=3306" \
  "DB_USER=vk_insight" \
  "DB_PASSWORD=vk_insight_password" \
  "DB_NAME=vk_insight" \
  "BASE_PATH=vk-insight"

wait -n
