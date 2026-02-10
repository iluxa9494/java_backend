CREATE DATABASE IF NOT EXISTS vk_users;
USE vk_users;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  interests TEXT NULL,
  PRIMARY KEY (id)
);

-- тестовые данные (можешь удалить)
INSERT INTO users (interests) VALUES
('music, sport, java'),
('java, docker, sport'),
('travel, music');