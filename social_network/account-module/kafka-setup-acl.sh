#!/bin/bash
echo "🔐 Setting up Kafka ACL permissions..."

# Ждем запуска Kafka
echo "⏳ Waiting for Kafka to start..."
sleep 30

# Настройка ACL для топика user-topic
# Разрешаем mc-authentication писать в топик
docker exec kafka-broker kafka-acls \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:mc-authentication \
  --operation Write \
  --topic user-topic

# Разрешаем mc-account читать из топика
docker exec kafka-broker kafka-acls \
  --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:mc-account \
  --operation Read \
  --topic user-topic

# Запрещаем всем остальным доступ к топику
docker exec kafka-broker kafka-acls \
  --bootstrap-server localhost:9092 \
  --add \
  --deny-principal User:* \
  --operation All \
  --topic user-topic

echo "✅ Kafka ACL permissions configured successfully!"
echo "📝 Summary:"
echo "   - mc-authentication can WRITE to user-topic"
echo "   - mc-account can READ from user-topic"
echo "   - All other users are DENIED"