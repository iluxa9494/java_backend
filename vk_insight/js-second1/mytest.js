const fs = require('fs');
const mysql = require('mysql2/promise');

async function exportInterests() {
  // Настройка подключения к базе данных
  const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: 'root',
    database: 'vk_users'
  });

  try {
    // Получаем все непустые интересы из базы
    const [rows] = await pool.query("SELECT interests FROM users WHERE interests IS NOT NULL AND interests <> ''");
    
    // Используем Set для хранения уникальных интересов
    const interestsSet = new Set();

    rows.forEach(row => {
      // Если интересы заданы, разбиваем строку по запятым
      if (row.interests) {
        row.interests.split(',').forEach(interest => {
          const trimmed = interest.trim();
          if (trimmed) {
            interestsSet.add(trimmed);
          }
        });
      }
    });

    // Преобразуем Set в массив и объединяем все элементы через запятую
    const uniqueInterests = Array.from(interestsSet);
    const result = uniqueInterests.join(', ');

    // Записываем результат в файл interests.txt
    fs.writeFileSync('interests.txt', result, 'utf8');
    console.log('Файл interests.txt создан!');
  } catch (error) {
    console.error('Ошибка:', error);
  } finally {
    await pool.end();
  }
}

exportInterests();
