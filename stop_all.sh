#!/bin/bash
echo "Останавливаем и удаляем все контейнеры из docker-compose.full.yml..."
docker compose -f docker-compose.full.yml down
echo "Всё остановлено и удалено."