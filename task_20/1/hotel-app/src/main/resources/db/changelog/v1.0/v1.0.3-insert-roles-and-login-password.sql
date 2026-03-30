-- Вставка ролей (идемпотентно по name)
INSERT INTO roles (name, description)
SELECT 'ROLE_ADMIN', 'Administrator with full access'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO roles (name, description)
SELECT 'ROLE_EMPLOYEE', 'Hotel employee with limited access'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_EMPLOYEE');

INSERT INTO roles (name, description)
SELECT 'ROLE_CLIENT', 'Hotel client with personal access'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_CLIENT');

-- Создаем тестового администратора (если есть person с id=1)
-- Пароль: admin123 (закодирован BCrypt)
-- Вставка пользователя (идемпотентно по username)
INSERT INTO users (person_id, username, password, email)
SELECT 1, 'admin', '$2a$10$XyosWxq4n1cXnzFod4id2.W5UQvZ3fwiV3b55Vrt.3jd48XWa/uNq', 'admin@hotel.com'
    WHERE EXISTS (SELECT 1 FROM persons WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Назначаем роль администратора (идемпотентно)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin'
  AND r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);