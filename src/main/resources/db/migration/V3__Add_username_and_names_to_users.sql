-- Добавляем новые колонки для поддержки Keycloak интеграции
ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(255);

-- Создаем уникальный индекс для username (где он не NULL)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON users(username) WHERE username IS NOT NULL;

-- Обновляем существующие записи (если есть)
UPDATE users SET username = email WHERE username IS NULL;
UPDATE users SET first_name = SPLIT_PART(full_name, ' ', 1) WHERE first_name IS NULL AND full_name IS NOT NULL;
UPDATE users SET last_name = SPLIT_PART(full_name, ' ', 2) WHERE last_name IS NULL AND full_name IS NOT NULL;

-- Делаем username NOT NULL после заполнения данных
ALTER TABLE users ALTER COLUMN username SET NOT NULL;


