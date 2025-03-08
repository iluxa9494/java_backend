#!/bin/bash
echo "Запуск сборки проекта..."
./gradlew clean build

echo "Запуск Docker-контейнеров..."
docker-compose up --build -d

echo "Бот запущен!"