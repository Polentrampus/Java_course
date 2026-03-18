INSERT INTO roles (name, description) VALUES
      ('ROLE_ADMIN', 'Administrator with full access'),
      ('ROLE_EMPLOYEE', 'Hotel employee with limited access'),
      ('ROLE_CLIENT', 'Hotel client with personal access');

-- Создаем тестового администратора (если есть person с id=1)
-- Пароль: admin123 (закодирован BCrypt)
INSERT INTO users (person_id, username, password, email)
SELECT 1, 'admin', '1', 'admin@hotel.com'
    WHERE EXISTS (SELECT 1 FROM persons WHERE id = 1);

-- Назначаем роль администратора
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';