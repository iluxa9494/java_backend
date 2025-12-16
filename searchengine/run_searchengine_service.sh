#!/usr/bin/env bash
set -euo pipefail

PROJECT_NAME="searchengine"
COMPOSE_FILE="docker-compose.yml"
APP_URL="http://localhost:18082"

POSTGRES_CONTAINER="searchengine-postgres"
APP_CONTAINER="searchengine-app"

# Определяем команду compose (docker compose / docker-compose)
if docker compose version >/dev/null 2>&1; then
  COMPOSE=(docker compose)
elif docker-compose version >/dev/null 2>&1; then
  COMPOSE=(docker-compose)
else
  echo "❌ Не найден docker compose / docker-compose"
  exit 1
fi

echo "🧩 Using compose command: ${COMPOSE[*]}"
echo "📁 Project dir: $(pwd)"
echo "📦 Compose file: $COMPOSE_FILE"
echo

echo "🧼 Stopping old containers (if any)..."
"${COMPOSE[@]}" -f "$COMPOSE_FILE" -p "$PROJECT_NAME" down --remove-orphans || true

echo "🔨 Building images..."
"${COMPOSE[@]}" -f "$COMPOSE_FILE" -p "$PROJECT_NAME" build

echo "🚀 Starting services..."
"${COMPOSE[@]}" -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up -d

echo
echo "⏳ Waiting for Postgres healthcheck..."
for i in {1..60}; do
  STATUS="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}' "$POSTGRES_CONTAINER" 2>/dev/null || true)"
  if [[ "$STATUS" == "healthy" ]]; then
    echo "✅ Postgres is healthy"
    break
  fi
  if [[ $i -eq 60 ]]; then
    echo "❌ Postgres did not become healthy in time (status: $STATUS)"
    echo "🧾 Postgres logs:"
    "${COMPOSE[@]}" -f "$COMPOSE_FILE" -p "$PROJECT_NAME" logs --no-color --tail=200 postgres || true
    exit 1
  fi
  sleep 2
done

echo
echo "⏳ Waiting for app endpoint /api/statistics..."
for i in {1..60}; do
  if curl -fsS "$APP_URL/api/statistics" >/dev/null 2>&1; then
    echo "✅ App is responding: $APP_URL/api/statistics"
    break
  fi
  if [[ $i -eq 60 ]]; then
    echo "❌ App did not start in time"
    echo "🧾 App logs:"
    "${COMPOSE[@]}" -f "$COMPOSE_FILE" -p "$PROJECT_NAME" logs --no-color --tail=300 app || true
    exit 1
  fi
  sleep 2
done

echo
echo "📌 Containers:"
"${COMPOSE[@]}" -f "$COMPOSE_FILE" -p "$PROJECT_NAME" ps

echo
echo "✅ Done."
echo "➡️  Open UI:  $APP_URL"
echo "➡️  Check API: $APP_URL/api/statistics"
echo
echo "🧾 To follow logs:"
echo "    ${COMPOSE[*]} -f \"$COMPOSE_FILE\" -p \"$PROJECT_NAME\" logs -f --tail=200 app"