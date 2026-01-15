CREATE DATABASE IF NOT EXISTS vk_insight;
USE vk_insight;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT UNSIGNED NOT NULL,
  first_name VARCHAR(255) NULL,
  last_name VARCHAR(255) NULL,
  age INT NULL,
  sex TINYINT NULL,
  city VARCHAR(255) NULL,
  interests TEXT NULL,
  education_level TEXT NULL,
  is_active TINYINT(1) NULL,
  posts_recent TINYINT(1) NULL,
  PRIMARY KEY (id)
);
