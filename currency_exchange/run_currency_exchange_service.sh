#!/usr/bin/env bash
set -euo pipefail

# -----------------------------
# run_currency_exchange_service.sh
# Запуск currency_exchange через Docker Compose:
# - билд + старт сервисов
# - опциональный сброс БД (RESET_DB=1)
# - ретрай билда при ошибке BuildKit snapshot
# - корректное ожидание старта контейнера (не падаем на "starting")
# -----------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"
APP_SERVICE="${APP_SERVICE:-currency_exchange}"
DB_SERVICE="${DB_SERVICE:-postgres}"

HOST_APP_PORT="${HOST_APP_PORT:-18080}"
CONTAINER_APP_PORT="${CONTAINER_APP_PORT:-8080}"

RESET_DB="${RESET_DB:-0}"       # 1 -> docker compose down -v
NO_CACHE="${NO_CACHE:-0}"       # 1 -> build --no-cache
DETACH="${DETACH:-1}"           # 1 -> up -d
FOLLOW_LOGS="${FOLLOW_LOGS:-1}" # 1 -> tail logs after start
START_TIMEOUT="${START_TIMEOUT:-180}" # seconds to wait app container to become running

if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "❌ Не найден $COMPOSE_FILE в директории: $SCRIPT_DIR"
  exit 1
fi

echo "📍 Workdir: $SCRIPT_DIR"
echo "📄 Compose: $COMPOSE_FILE"
echo "🔧 Services: $DB_SERVICE + $APP_SERVICE"
echo "🌐 App URL: http://localhost:${HOST_APP_PORT} (container:${CONTAINER_APP_PORT})"

down_cmd=(docker compose -f "$COMPOSE_FILE" down --remove-orphans)
up_cmd=(docker compose -f "$COMPOSE_FILE" up)
build_cmd=(docker compose -f "$COMPOSE_FILE" build)

if [[ "$RESET_DB" == "1" ]]; then
  echo "🧨 RESET_DB=1 -> останавливаю и удаляю volume-ы БД (down -v)"
  docker compose -f "$COMPOSE_FILE" down -v --remove-orphans || true
else
  echo "🧹 Останавливаю прошлые контейнеры (без удаления volume-ов)"
  "${down_cmd[@]}" || true
fi

if [[ "$NO_CACHE" == "1" ]]; then
  echo "🏗️  Build (no-cache)"
  build_cmd+=(--no-cache)
else
  echo "🏗️  Build"
fi

build_with_retry() {
  set +e
  local out
  out="$("${build_cmd[@]}" 2>&1)"
  local code=$?
  set -e

  if [[ $code -eq 0 ]]; then
    echo "$out"
    return 0
  fi

  echo "$out"

  # Ловим типичную ошибку BuildKit:
  # failed to prepare extraction snapshot ... parent snapshot ... does not exist
  if echo "$out" | grep -qE "failed to prepare extraction snapshot|parent snapshot .* does not exist"; then
    echo "⚠️  Похоже на проблему BuildKit snapshot. Делаю docker builder prune и повторяю билд 1 раз..."
    docker builder prune -af || true

    echo "🏗️  Retry build..."
    "${build_cmd[@]}"
    return $?
  fi

  return $code
}

build_with_retry

if [[ "$DETACH" == "1" ]]; then
  echo "🚀 Up (detached)"
  "${up_cmd[@]}" -d
else
  echo "🚀 Up (foreground)"
  "${up_cmd[@]}"
  exit 0
fi

echo "⏳ Жду пока сервисы поднимутся..."
sleep 2

echo "📌 Статус контейнеров:"
docker compose -f "$COMPOSE_FILE" ps

# --- Надёжное ожидание старта APP_SERVICE ---
app_cid="$(docker compose -f "$COMPOSE_FILE" ps -q "$APP_SERVICE" || true)"
if [[ -z "${app_cid}" ]]; then
  echo "❌ Не смог получить container id для сервиса $APP_SERVICE"
  docker compose -f "$COMPOSE_FILE" ps
  exit 1
fi

echo "⏳ Жду пока $APP_SERVICE перейдёт в Running (timeout=${START_TIMEOUT}s)..."
deadline=$((SECONDS + START_TIMEOUT))

while (( SECONDS < deadline )); do
  running="$(docker inspect -f '{{.State.Running}}' "$app_cid" 2>/dev/null || echo "false")"
  exit_code="$(docker inspect -f '{{.State.ExitCode}}' "$app_cid" 2>/dev/null || echo "0")"

  if [[ "$running" == "true" ]]; then
    echo "✅ $APP_SERVICE в состоянии Running"
    break
  fi

  # Если контейнер уже завершился с ошибкой — сразу показываем логи и выходим
  if [[ "$running" == "false" && "$exit_code" != "0" ]]; then
    echo "❌ $APP_SERVICE завершился с кодом $exit_code. Показываю логи:"
    docker compose -f "$COMPOSE_FILE" logs --no-color --tail=300 "$APP_SERVICE" || true
    exit 1
  fi

  sleep 2
done

if [[ "$(docker inspect -f '{{.State.Running}}' "$app_cid" 2>/dev/null || echo "false")" != "true" ]]; then
  echo "❌ $APP_SERVICE не перешёл в Running за ${START_TIMEOUT}s. Показываю логи:"
  docker compose -f "$COMPOSE_FILE" logs --no-color --tail=300 "$APP_SERVICE" || true
  exit 1
fi

echo "✅ $APP_SERVICE запущен. URL: http://localhost:${HOST_APP_PORT}"

if [[ "$FOLLOW_LOGS" == "1" ]]; then
  echo "🧾 Tail logs (Ctrl+C чтобы выйти, контейнеры останутся запущенными)"
  docker compose -f "$COMPOSE_FILE" logs -f --tail=200 "$APP_SERVICE"
fi