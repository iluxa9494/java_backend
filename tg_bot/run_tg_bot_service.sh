#!/usr/bin/env bash
set -euo pipefail

# Запуск: ./run_tg_bot_service.sh up|down|restart|logs|ps
ACTION="${1:-up}"

# docker compose (v2) или docker-compose (v1)
if docker compose version >/dev/null 2>&1; then
  DC="docker compose"
elif command -v docker-compose >/dev/null 2>&1; then
  DC="docker-compose"
else
  echo "❌ Не найден Docker Compose. Установи Docker Desktop (macOS) или docker-compose-plugin (Linux)."
  exit 1
fi

case "$ACTION" in
  up)
    echo "▶️  Starting tg_bot stack..."
    $DC up -d --build
    echo "✅ Done. Use: $0 logs"
    ;;
  down)
    echo "⏹  Stopping tg_bot stack..."
    $DC down
    ;;
  restart)
    echo "🔄 Restarting tg_bot stack..."
    $DC down
    $DC up -d --build
    ;;
  logs)
    $DC logs -f --tail=200 bot db
    ;;
  ps)
    $DC ps
    ;;
  *)
    echo "Usage: $0 {up|down|restart|logs|ps}"
    exit 2
    ;;
esac