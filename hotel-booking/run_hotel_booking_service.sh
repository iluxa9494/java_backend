#!/usr/bin/env bash
set -euo pipefail

# === Config ===
COMPOSE_FILE="docker-compose.yml"
PROJECT_NAME="hotel-booking"
APP_SERVICE="hotel-booking"
APP_CONTAINER="hotel-booking-app"
HTTP_HOST_PORT="8082"
HEALTH_TIMEOUT_SEC=180
SLEEP_STEP_SEC=2

usage() {
  cat <<'USAGE'
Usage:
  ./run_hotel_booking_service.sh [options]

Options:
  --fresh     Stop + remove volumes (clean DB) before start
  --down      Stop services (docker compose down)
  --logs      Follow logs after start
  --build     Force rebuild images
  -h,--help   Show this help
USAGE
}

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || { echo "❌ Required command not found: $1"; exit 1; }
}

ensure_in_project_root() {
  if [[ ! -f "$COMPOSE_FILE" ]]; then
    echo "❌ $COMPOSE_FILE not found in current directory: $(pwd)"
    echo "➡️  Run this script from the project root (where docker-compose.yml is)."
    exit 1
  fi
}

wait_healthy() {
  local container_name="$1"
  local timeout="${2:-$HEALTH_TIMEOUT_SEC}"

  echo "⏳ Waiting for container to become healthy: $container_name (timeout ${timeout}s)"
  local start_ts
  start_ts="$(date +%s)"

  while true; do
    local status=""
    status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}' "$container_name" 2>/dev/null || true)"

    if [[ "$status" == "healthy" || "$status" == "no-healthcheck" ]]; then
      echo "✅ $container_name status: $status"
      return 0
    fi

    # If container is not running -> fail fast
    local running=""
    running="$(docker inspect -f '{{.State.Running}}' "$container_name" 2>/dev/null || echo "false")"
    if [[ "$running" != "true" ]]; then
      echo "❌ $container_name is not running. Last logs:"
      docker logs --tail=120 "$container_name" || true
      return 1
    fi

    local now_ts
    now_ts="$(date +%s)"
    if (( now_ts - start_ts > timeout )); then
      echo "❌ Timeout waiting for $container_name to be healthy. Last logs:"
      docker logs --tail=200 "$container_name" || true
      return 1
    fi

    sleep "$SLEEP_STEP_SEC"
  done
}

# === Parse args ===
FRESH=0
DOWN=0
FOLLOW_LOGS=0
FORCE_BUILD=0

# NOTE: use "$@" (not "${@:-}") so that running the script with no args
# does not produce a single empty argument and trigger the "Unknown option" branch.
for arg in "$@"; do
  case "$arg" in
    --fresh) FRESH=1 ;;
    --down) DOWN=1 ;;
    --logs) FOLLOW_LOGS=1 ;;
    --build) FORCE_BUILD=1 ;;
    -h|--help) usage; exit 0 ;;
    *)
      echo "❌ Unknown option: $arg"
      usage
      exit 1
      ;;
  esac
done

# === Checks ===
need_cmd docker
need_cmd awk
ensure_in_project_root

# docker compose can be either "docker compose" or "docker-compose"
COMPOSE_CMD="docker compose"
if ! $COMPOSE_CMD version >/dev/null 2>&1; then
  need_cmd docker-compose
  COMPOSE_CMD="docker-compose"
fi

echo "🧩 Using compose command: $COMPOSE_CMD"
echo "📁 Project dir: $(pwd)"

if (( DOWN == 1 )); then
  echo "🧹 Stopping services..."
  $COMPOSE_CMD down
  echo "✅ Done."
  exit 0
fi

if (( FRESH == 1 )); then
  echo "🧨 Fresh start requested: stopping and removing volumes..."
  $COMPOSE_CMD down -v
fi

# === Start ===
UP_ARGS=(up -d)
if (( FORCE_BUILD == 1 )); then
  UP_ARGS+=(--build)
fi

echo "🚀 Starting services..."
$COMPOSE_CMD "${UP_ARGS[@]}"

echo
$COMPOSE_CMD ps
echo

# === Wait infra ===
wait_healthy "hotel-booking-postgres"
wait_healthy "hotel-booking-mongodb"
wait_healthy "hotel-booking-kafka"

echo
echo "✅ Infrastructure is ready."

# === Optional: show app logs if app is not yet running ===
if ! docker inspect -f '{{.State.Running}}' "$APP_CONTAINER" >/dev/null 2>&1; then
  echo "⚠️ App container not found yet: $APP_CONTAINER"
else
  echo "📌 App container status:"
  docker inspect -f 'running={{.State.Running}} health={{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}' "$APP_CONTAINER" || true
fi

echo
echo "🌐 Service URLs:"
echo "  - App:      http://localhost:${HTTP_HOST_PORT}"
echo "  - Actuator: http://localhost:${HTTP_HOST_PORT}/actuator/health"
echo "  - Swagger:  http://localhost:${HTTP_HOST_PORT}/swagger-ui/index.html  (if enabled)"
echo

if (( FOLLOW_LOGS == 1 )); then
  echo "📜 Following logs for service: $APP_SERVICE"
  $COMPOSE_CMD logs -f "$APP_SERVICE"
else
  echo "ℹ️ To follow logs: $COMPOSE_CMD logs -f $APP_SERVICE"
fi