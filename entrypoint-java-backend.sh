#!/usr/bin/env bash
set -euo pipefail

log() {
  echo "[$(date +'%H:%M:%S')] $*"
}

JB_CONFIG_DIR="${JB_CONFIG_DIR:-/config}"
EUREKA_URL="${EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE:-http://eureka-server:8761/eureka/}"

JAVA_XMX="${JAVA_XMX:-}"
JAVA_XMS="${JAVA_XMS:-${JAVA_XMX}}"
JAVA_MAX_METASPACE="${JAVA_MAX_METASPACE:-}"
JAVA_GC_OPTS="${JAVA_GC_OPTS:--XX:+UseG1GC -XX:MaxGCPauseMillis=200}"
JAVA_TOOL_OPTIONS_BASE="${JAVA_TOOL_OPTIONS_BASE:-}"

if [[ -z "${JAVA_TOOL_OPTIONS:-}" ]]; then
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS_BASE}"
  if [[ -n "${JAVA_XMS}" ]]; then
    JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -Xms${JAVA_XMS}"
  fi
  if [[ -n "${JAVA_XMX}" ]]; then
    JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -Xmx${JAVA_XMX}"
  fi
  if [[ -n "${JAVA_MAX_METASPACE}" ]]; then
    JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -XX:MaxMetaspaceSize=${JAVA_MAX_METASPACE}"
  fi
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} ${JAVA_GC_OPTS}"
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

# Social Network (modular monolith)
start_java "social-network" "/app/social-network.jar" "8003" \
  "${JB_CONFIG_DIR}/social_network/.env"

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
