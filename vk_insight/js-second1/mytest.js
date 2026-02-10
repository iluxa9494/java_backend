const fs = require('fs');
const http = require('http');
const mysql = require('mysql2/promise');

const dbConfig = {
  host: process.env.DB_HOST || '127.0.0.1',
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'vk_insight'
};

async function ensureSchema() {
  const conn = await mysql.createConnection({
    host: dbConfig.host,
    port: dbConfig.port,
    user: dbConfig.user,
    password: dbConfig.password,
    multipleStatements: true
  });

  await conn.query(`CREATE DATABASE IF NOT EXISTS \`${dbConfig.database}\`;`);
  await conn.query(`USE \`${dbConfig.database}\`;`);
  await conn.query(`
    CREATE TABLE IF NOT EXISTS users (
      id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
      interests TEXT NULL,
      PRIMARY KEY (id)
    );
  `);

  await conn.end();
}

async function exportInterests(pool) {
  try {
    const [rows] = await pool.query(
      "SELECT interests FROM users WHERE interests IS NOT NULL AND interests <> ''"
    );

    const interestsSet = new Set();
    rows.forEach((row) => {
      if (row.interests) {
        row.interests.split(',').forEach((interest) => {
          const trimmed = String(interest).trim();
          if (trimmed) interestsSet.add(trimmed);
        });
      }
    });

    const uniqueInterests = Array.from(interestsSet);
    fs.writeFileSync('interests.txt', uniqueInterests.join(', '), 'utf8');
    console.log(`Файл interests.txt создан! (unique: ${uniqueInterests.length})`);
  } catch (error) {
    console.error('Ошибка:', error);
  }
}

async function start() {
  try {
    await ensureSchema();
  } catch (error) {
    console.error('Ошибка инициализации БД:', error);
  }

  const pool = mysql.createPool({
    host: dbConfig.host,
    port: dbConfig.port,
    user: dbConfig.user,
    password: dbConfig.password,
    database: dbConfig.database,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
  });

  await exportInterests(pool);

  const server = http.createServer(async (req, res) => {
    if (req.url === '/') {
      res.statusCode = 200;
      res.setHeader('Content-Type', 'text/html; charset=utf-8');
      res.end(`<!doctype html>
<html lang="ru">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>VK Insight</title>
  </head>
  <body>
    <h1>VK Insight</h1>
    <p>Service is running.</p>
  </body>
</html>`);
      return;
    }

    if (req.url && req.url.startsWith('/health')) {
      try {
        await pool.query('SELECT 1');
        res.statusCode = 200;
        res.end('ok');
      } catch (error) {
        res.statusCode = 500;
        res.end('db error');
      }
      return;
    }

    res.statusCode = 404;
    res.end('not found');
  });

  const port = Number(process.env.PORT || 8084);
  server.listen(port, '0.0.0.0', () => {
    console.log(`vk-insight listening on ${port}`);
  });
}

start();
