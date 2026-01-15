const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');
const { VK } = require('vk-io');

const app = express();
app.disable('x-powered-by');
const PORT = Number(process.env.PORT || 3000);
const BASE_PATH = (process.env.BASE_PATH || '').trim();
const normalizedBasePath = BASE_PATH
  ? `/${BASE_PATH.replace(/^\/+|\/+$/g, '')}`
  : '';
global.searchProgress = 0;
let progressClients = [];

function broadcastProgress(progress) {
  progressClients.forEach(client => {
    client.write(`data: ${progress}\n\n`);
  });
}

app.get('/progress', (req, res) => {
  res.set({
    'Content-Type': 'text/event-stream',
    'Cache-Control': 'no-cache',
    Connection: 'keep-alive'
  });
  res.flushHeaders();
  res.write('retry: 10000\n\n');
  progressClients.push(res);
  req.on('close', () => {
    progressClients = progressClients.filter(client => client !== res);
  });
});

const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || 'root',
  database: process.env.DB_NAME || 'vk_insight'
};

const db = mysql.createPool(dbConfig);

app.use(bodyParser.json({ limit: '1mb' }));
app.use(bodyParser.urlencoded({ extended: true, limit: '1mb' }));

if (normalizedBasePath) {
  app.use((req, res, next) => {
    if (req.url === normalizedBasePath) {
      req.url = '/';
      return next();
    }
    if (req.url.startsWith(`${normalizedBasePath}/`)) {
      req.url = req.url.slice(normalizedBasePath.length) || '/';
      return next();
    }
    return next();
  });
}

app.use(express.static(path.join(__dirname, 'public')));

app.get('/health', async (req, res) => {
  try {
    await db.query('SELECT 1');
    res.status(200).send('ok');
  } catch (err) {
    res.status(500).send('db error');
  }
});

/** Вспомогательная функция, получающая группы, где состоит userId */
async function getUserGroups(userId, count, offset = 0) {
  try {
    const groups = await global.vk.api.groups.get({
      user_id: userId,
      extended: 1,
      count,
      fields: ['activity'],
      offset
    });
    return groups.items;
  } catch (err) {
    // просто подавим ошибку
  }
}

/** Если нет user.interests, пытаемся вытащить activity из групп */
async function get_interest_by_group(user_id, group_count = 100) {
  const result = [];
  const groups = await getUserGroups(user_id, group_count);
  if (groups) {
    for (let i = 0; i < groups.length; i++) {
      const activity = groups[i].activity;
      if (activity && !result.includes(activity) && !/\d/.test(activity)) {
        result.push(activity);
      }
    }
    return result.join(',');
  }
  return undefined;
}

/** Отправка сообщений */
app.post('/send-message', async (req, res) => {
  if (!global.vk) {
    return res.status(400).send('VK API не инициализирован.');
  }

  const data = Array.isArray(req.body) ? req.body : [];
  const messageTemplate = data[0];
  const rows = data.slice(1).filter(row => Array.isArray(row) && row.length >= 2);

  if (typeof messageTemplate !== 'string' || messageTemplate.trim() === '') {
    return res.status(400).send('Сообщение не задано.');
  }

  if (rows.length === 0) {
    return res.status(400).send('Нет получателей.');
  }

  try {
    const promises = rows.map(async ([text, id, ...userInfo], index) => {
      // Каждые 4 сообщения — пауза, чтобы не упираться в ограничения
      if (index % 4 === 0) {
        await new Promise(resolve => setTimeout(resolve, 1020));
      }
      await global.vk.api.messages.send({
        user_id: parseInt(id, 10),
        message: messageTemplate.split("Имя Фамилия").join(`${userInfo[0]} ${userInfo[1]}`),
        random_id: Date.now()
      });
      // Удаляем запись из БД (если хотите), чтобы не отправлять повторно
      await db.query('DELETE FROM users WHERE id = ?', [parseInt(id, 10)]);
    });
    await Promise.all(promises);
    res.sendStatus(200);
  } catch (err) {
    console.error('Ошибка при отправке сообщений:', err);
    res.status(500).send('Ошибка при отправке сообщений.');
  }
});

/** Поиск пользователей */
function getGroupId(groupUrl) {
  if (!groupUrl || typeof groupUrl !== 'string') return null;
  const parts = groupUrl.split('/').filter(Boolean);
  return parts.length ? parts[parts.length - 1] : null;
}

app.post('/search', async (req, res) => {
  const { apiVk, groupUrl, age, sex, city } = req.body;
  const groupId = getGroupId(groupUrl);
  const parsedAge = parseInt(age, 10);

  if (!apiVk || typeof apiVk !== 'string') {
    return res.status(400).send('API токен не задан.');
  }
  if (!groupId) {
    return res.status(400).send('Некорректная ссылка на группу.');
  }
  if (Number.isNaN(parsedAge) || parsedAge <= 0) {
    return res.status(400).send('Некорректный возраст.');
  }

  global.apiVk = apiVk;
  global.vk = new VK({ token: global.apiVk });
  try {
    let offset = 0;
    const members = [];
    // Установим условный лимит — скажем, до 300k
    let totalCount = 300000;

    while (true) {
      const membersResponse = await global.vk.api.groups.getMembers({
        group_id: groupId,
        fields: [
          'bdate',
          'sex',
          'city',
          'online',
          'universities',
          'schools',
          'deactivated',
          'last_seen'
        ],
        v: '5.131',
        offset
      });
      const newMembers = membersResponse.items;
      members.push(...newMembers);

      // Обновляем прогресс (максимум 50%)
      global.searchProgress = (members.length / totalCount) * 50;
      broadcastProgress(global.searchProgress);

      // Если вернулось меньше 1000 — кончились
      if (newMembers.length < 1000) break;
      offset += 1000;
      if (offset >= totalCount) break;
    }

    // Фильтрация по возрасту, полу, городу, активности
    const filteredMembers = members.filter(user => {
      if (user.deactivated) return false;
      if (!user.bdate) return false;

      const bdateParts = user.bdate.split('.');
      if (bdateParts.length !== 3) return false;

      const birthYear = parseInt(bdateParts[2], 10);
      const currentYear = new Date().getFullYear();
      const userAge = currentYear - birthYear;

      // Активен: онлайн или был не позже 5 ч назад
		let isActive = false;
		if (user.online === 1) {
		  isActive = true;
		} else if (user.last_seen && user.last_seen.time) {
		  const lastSeenTime = user.last_seen.time * 1000;
		  const hoursSinceLastSeen = (Date.now() - lastSeenTime) / 3600000;
		  isActive = (hoursSinceLastSeen <= 5);
		}

      return userAge <= parseInt(age, 10) &&
             (sex === undefined || user.sex === parseInt(sex, 10)) &&
             (city === undefined || (user.city && user.city.title === city)) &&
             isActive;
    });

    // *****************
    // ДОБАВЛЯЕМ Проверку can_write_private_message
    // *****************
    const BATCH_SIZE = 100;
    const finalMembers = [];

    for (let i = 0; i < filteredMembers.length; i += BATCH_SIZE) {
      // Берём часть массива по BATCH_SIZE
      const chunk = filteredMembers.slice(i, i + BATCH_SIZE);
      const userIds = chunk.map(u => u.id).join(',');

      // Запрашиваем, можно ли писать каждому
      const usersInfo = await global.vk.api.users.get({
        user_ids: userIds,
        fields: ['can_write_private_message']
      });

      // Оставляем только тех, кому can_write_private_message === 1
      for (let j = 0; j < chunk.length; j++) {
        const userObj = chunk[j];
        const info = usersInfo.find(ui => ui.id === userObj.id);
        if (info && info.can_write_private_message === 1) {
          finalMembers.push(userObj);
        }
      }
    }
    // Теперь вместо filteredMembers используем finalMembers
    // *****************

    let addedCount = 0;

    // Обработка finalMembers вместо filteredMembers
    for (let i = 0; i < finalMembers.length; i++) {
      const user = finalMembers[i];
      const bdateParts = user.bdate.split('.');
      const userAge = new Date().getFullYear() - parseInt(bdateParts[2], 10);

      // 1) Получаем интересы, если пусто
      if (!user.interests) {
        user.interests = await get_interest_by_group(user.id);
      }

      // 2) Собираем детальную информацию о вузах и школах
      let universitiesStr = "";
      if (user.universities && user.universities.length > 0) {
        universitiesStr = user.universities
          .map(u => {
            return `${u.country || ''}, ${u.city || ''}, ${u.name || ''}, ${u.faculty || ''}, ${u.graduation || ''}`;
          })
          .join('; ');
      }

      let schoolsStr = "";
      if (user.schools && user.schools.length > 0) {
        schoolsStr = user.schools
          .map(s => {
            return `${s.country || ''}, ${s.city || ''}, ${s.name || ''}, ${s.year_from || ''}, ${s.year_to || ''}`;
          })
          .join('; ');
      }

      // Формируем единую строку
      let educationDetail = "";
      if (universitiesStr) {
        educationDetail += `Университеты: ${universitiesStr}`;
      }
      if (schoolsStr) {
        educationDetail += (educationDetail ? " | " : "") + `Школы: ${schoolsStr}`;
      }

      // 3) Определяем education_level
      let education_level = undefined;
      if (user.universities && user.universities.length > 0) {
        for (let j = 0; j < user.universities.length; j++) {
          if (user.universities[j].education_status) {
            education_level = user.universities[j].education_status;
            break;
          }
        }
        if (!education_level) {
          if (user.universities.length > 2) {
            education_level = "Аспирант";
          } else if (user.universities.length === 2) {
            education_level = "Магистр";
          } else if (user.universities.length === 1) {
            education_level = "Бакалавр или специалитет";
          }
        }
      } else if (user.schools && user.schools.length > 0) {
        for (let k = 0; k < user.schools.length; k++) {
          if (user.schools[k].type_str) {
            education_level = "Колледж";
            break;
          }
        }
      }
      if (!education_level && userAge > 16 && userAge <= 18) {
        education_level = "Школа";
      }

      // 4) Проверяем посты => postsRecent
      let postsRecent = false;
      try {
        const postsResponse = await global.vk.api.wall.get({ owner_id: user.id, count: 2 });
        if (postsResponse.items.length >= 2) {
          const postDate1 = new Date(postsResponse.items[0].date * 1000);
          const postDate2 = new Date(postsResponse.items[1].date * 1000);
          const currentDate = new Date();
          const dayDifference1 = (currentDate - postDate1) / (1000*60*60*24);
          const dayDifference2 = (currentDate - postDate2) / (1000*60*60*24);
          const dayDifferenceBetweenPosts = Math.abs(postDate1 - postDate2) / (1000*60*60*24);
          postsRecent = (dayDifference1 <= 7) && (dayDifference2 <= 7) && (dayDifferenceBetweenPosts <= 7);
        }
      } catch (errWall) {
        // Игнорируем ошибки доступа
      }

      // 5) Собираем finalEducationLevel
      let finalEducationLevel = education_level || "";
      if (educationDetail) {
        finalEducationLevel += (finalEducationLevel ? " | " : "") + educationDetail;
      }

      const isActive = user.online === 1 ||
        (user.last_seen && ((Date.now() - user.last_seen.time * 1000) / (1000*60*60*24) <= 3));

      try {
        await db.query(
          `INSERT INTO users
            (id, first_name, last_name, age, sex, city, interests, education_level, is_active, posts_recent)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
              first_name = VALUES(first_name),
              last_name = VALUES(last_name),
              age = VALUES(age),
              sex = VALUES(sex),
              city = VALUES(city),
              interests = VALUES(interests),
              education_level = VALUES(education_level),
              is_active = VALUES(is_active),
              posts_recent = VALUES(posts_recent)`,
          [
            user.id,
            user.first_name,
            user.last_name,
            userAge,
            user.sex,
            user.city && user.city.title,
            user.interests || null,
            finalEducationLevel || null,
            isActive,
            postsRecent
          ]
        );
        addedCount++;
      } catch (dbErr) {
        console.error('Ошибка при сохранении пользователя:', dbErr);
      }

      // Обновляем прогресс от 50% до 100%
      global.searchProgress = 50 + ((i + 1) / finalMembers.length) * 50;
      broadcastProgress(global.searchProgress);
    }

    // Завершение
    global.searchProgress = 100;
    broadcastProgress(global.searchProgress);
    res.json({ addedCount });
  } catch (err) {
    console.error('Ошибка при поиске пользователей:', err);
    res.status(500).send('Ошибка при запросе данных.');
  }
});

/** Количество пользователей */
app.get('/user-count', async (req, res) => {
  try {
    const [rows] = await db.query('SELECT COUNT(*) AS count FROM users');
    res.json({ count: rows[0].count });
  } catch (err) {
    console.error('Ошибка получения количества пользователей:', err);
    res.status(500).send('Ошибка сервера.');
  }
});

/** Загрузка всех пользователей */
app.get('/load-users', async (req, res) => {
  try {
    const [rows] = await db.query('SELECT * FROM users');
    res.json(rows);
  } catch (err) {
    console.error('Ошибка при загрузке пользователей:', err);
    res.status(500).send('Ошибка при загрузке пользователей.');
  }
});

/** Работа с ручными классификациями */
const classificationsFile = path.join(__dirname, 'classifications.json');
let manualClassifications = {};
if (fs.existsSync(classificationsFile)) {
  try {
    manualClassifications = JSON.parse(fs.readFileSync(classificationsFile, 'utf8'));
  } catch {}
}
function saveManualClassifications() {
  fs.writeFile(classificationsFile, JSON.stringify(manualClassifications, null, 2), () => {});
}

app.get('/manual-classifications', (req, res) => {
  res.json(manualClassifications);
});

app.post('/manual-classifications', (req, res) => {
  const { newClass, newSubclass, keywords, city, age } = req.body;
  if (!newClass || !newSubclass) {
    return res.status(400).json({ error: 'Новый класс и подкласс обязательны' });
  }
  if (!manualClassifications[newClass]) {
    manualClassifications[newClass] = {};
  }
  if (manualClassifications[newClass][newSubclass]) {
    return res.status(400).json({ error: 'Такой подкласс уже существует' });
  }
  manualClassifications[newClass][newSubclass] = {
    keywords: Array.isArray(keywords) ? keywords : [],
    city: city || null,
    age: age ? parseInt(age, 10) : null
  };
  saveManualClassifications();
  res.json({ message: 'Добавлено', manualClassifications });
});

app.put('/manual-classifications/:class/:sub', (req, res) => {
  const { class: cls, sub } = req.params;
  const { keywords, city, age } = req.body;
  if (!manualClassifications[cls] || !manualClassifications[cls][sub]) {
    return res.status(404).json({ error: 'Не найдено' });
  }
  manualClassifications[cls][sub] = {
    keywords: Array.isArray(keywords) ? keywords : [],
    city: city || null,
    age: age ? parseInt(age, 10) : null
  };
  saveManualClassifications();
  res.json({ message: 'Обновлено', manualClassifications });
});

app.delete('/manual-classifications/:class/:sub', (req, res) => {
  const { class: cls, sub } = req.params;
  if (!manualClassifications[cls] || !manualClassifications[cls][sub]) {
    return res.status(404).json({ error: 'Не найдено' });
  }
  delete manualClassifications[cls][sub];
  if (!Object.keys(manualClassifications[cls]).length) {
    delete manualClassifications[cls];
  }
  saveManualClassifications();
  res.json({ message: 'Удалено', manualClassifications });
});

app.listen(PORT, () => {
  console.log(`Сервер запущен на http://localhost:${PORT}`);
});
