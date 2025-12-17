#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

echo "== up mysql =="
docker compose up -d --remove-orphans mysql

echo "== wait mysql healthy =="
for i in {1..60}; do
  st="$(docker inspect -f '{{.State.Health.Status}}' vk_insight_mysql 2>/dev/null || true)"
  if [[ "$st" == "healthy" ]]; then
    echo "MySQL is healthy"
    break
  fi
  sleep 1
done

if [[ "$(docker inspect -f '{{.State.Health.Status}}' vk_insight_mysql 2>/dev/null || true)" != "healthy" ]]; then
  echo "ERROR: MySQL did not become healthy"
  docker logs --tail=200 vk_insight_mysql || true
  exit 2
fi

echo "== ensure db/user (caching_sha2_password) =="
docker exec -i vk_insight_mysql mysql -uroot -ppassword -e "
CREATE DATABASE IF NOT EXISTS vk_users;
CREATE USER IF NOT EXISTS 'vk'@'%' IDENTIFIED WITH caching_sha2_password BY 'vk';
GRANT ALL PRIVILEGES ON vk_users.* TO 'vk'@'%';
FLUSH PRIVILEGES;
" >/dev/null

export DB_HOST="127.0.0.1"
export DB_PORT="3306"
export DB_NAME="vk_users"
export DB_USER="vk"
export DB_PASSWORD="vk"

echo "== run node =="
cd js-second1
node mytest.js