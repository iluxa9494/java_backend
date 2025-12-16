#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

echo "[1/4] Build image (dev)"
# полная пересборка: NO_CACHE=1 ./run_tariff_calculator.sh
if [[ "${NO_CACHE:-0}" == "1" ]]; then
  docker compose build --no-cache app
else
  docker compose build app
fi

echo "[2/4] Up postgres + app"
docker compose up -d postgres app

echo "[3/4] Status"
docker compose ps

echo "[4/4] Tail logs"
docker compose logs -n 120 --tail 120 app

echo
echo "App:        http://localhost:8001"
echo "Postgres:   localhost:15432"
echo "Debug port: localhost:7001"
echo
echo "Stop:       docker compose down"
echo "Logs:       docker compose logs -f app"