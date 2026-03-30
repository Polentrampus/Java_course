-- Таблица пользователей системы
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    person_id INTEGER UNIQUE REFERENCES persons(id) ON DELETE CASCADE,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
    );

-- Связь пользователей с ролями
CREATE TABLE IF NOT EXISTS user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
    );

-- Таблица привилегий/прав
CREATE TABLE IF NOT EXISTS privileges (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255)
    );

-- Связь ролей с привилегиями
CREATE TABLE IF NOT EXISTS role_privileges (
    role_id INTEGER NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    privilege_id INTEGER NOT NULL REFERENCES privileges(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, privilege_id)
    );

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_users_username') THEN
CREATE INDEX idx_users_username ON users(username);
END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_users_person_id') THEN
CREATE INDEX idx_users_person_id ON users(person_id);
END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_roles_user_id') THEN
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
END IF;
END $$;