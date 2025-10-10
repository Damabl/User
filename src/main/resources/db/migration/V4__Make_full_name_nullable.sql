-- Делаем поле full_name nullable, так как теперь используем firstName и lastName
ALTER TABLE users ALTER COLUMN full_name DROP NOT NULL;


