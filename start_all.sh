#!/bin/bash
echo "Запускаем все проекты из docker-compose.full.yml..."
docker compose -f docker-compose.full.yml up --build -d
echo "Все сервисы запущены в фоне."