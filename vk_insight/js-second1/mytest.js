const fs = require('fs');
const mysql = require('mysql2/promise');

async function exportInterests() {
  // Настройка подключения к базе данных через переменные окружения
  // (работает и локально, и в Docker: DB_HOST=mysql)
  const pool = mysql.createPool({
    host: process.env.DB_HOST || '127.0.0.1',
    port: Number(process.env.DB_PORT || 3306),
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'vk_users',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
  });

  try {
    // Получаем все непустые интересы из базы
    const [rows] = await pool.query(
      "SELECT interests FROM users WHERE interests IS NOT NULL AND interests <> ''"
    );

    // Используем Set для хранения уникальных интересов
    const interestsSet = new Set();

    rows.forEach((row) => {
      if (row.interests) {
        row.interests.split(',').forEach((interest) => {
          const trimmed = String(interest).trim();
          if (trimmed) interestsSet.add(trimmed);
        });
      }
    });

    // Преобразуем Set в массив и объединяем все элементы через запятую
    const uniqueInterests = Array.from(interestsSet);
    const result = uniqueInterests.join(', ');

    // Записываем результат в файл interests.txt (рядом со скриптом)
    fs.writeFileSync('interests.txt', result, 'utf8');
    console.log(`Файл interests.txt создан! (unique: ${uniqueInterests.length})`);
  } catch (error) {
    console.error('Ошибка:', error);
    process.exitCode = 1;
  } finally {
    await pool.end();
  }
}

exportInterests();