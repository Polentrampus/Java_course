-- Вставка сотрудников (идемпотентно)
INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
SELECT 'employee', 'Ivan', 'Ivanov', 'Ivanovich', '1985-05-15'
WHERE NOT EXISTS (SELECT 1 FROM persons WHERE id = 1);

INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
SELECT 'employee', 'Maria', 'Petrova', 'Sergeevna', '1990-08-22'
WHERE NOT EXISTS (SELECT 1 FROM persons WHERE id = 2);

INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
SELECT 'employee', 'Alexey', 'Sidorov', 'Vladimirovich', '1988-03-10'
WHERE NOT EXISTS (SELECT 1 FROM persons WHERE id = 3);

-- Вставка в employees (идемпотентно)
INSERT INTO employees (id, position, hire_date, salary)
SELECT 1, 'ADMIN', '2020-01-15', 50000.00
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE id = 1);

INSERT INTO employees (id, position, hire_date, salary)
SELECT 2, 'MAID', '2021-03-20', 35000.00
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE id = 2);

INSERT INTO employees (id, position, hire_date, salary)
SELECT 3, 'MENDER', '2019-11-10', 45000.00
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE id = 3);

-- Вставка клиентов (идемпотентно)
INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
SELECT 'client', 'Anna', 'Smirnova', 'Alexandrovna', '1992-07-30'
WHERE NOT EXISTS (SELECT 1 FROM persons WHERE id = 4);

INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
SELECT 'client', 'Dmitry', 'Kuznetsov', 'Petrovich', '1987-12-14'
WHERE NOT EXISTS (SELECT 1 FROM persons WHERE id = 5);

-- Вставка в clients (идемпотентно)
INSERT INTO clients (id)
SELECT 4
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE id = 4);

INSERT INTO clients (id)
SELECT 5
WHERE NOT EXISTS (SELECT 1 FROM clients WHERE id = 5);

-- Вставка услуг (идемпотентно по name, так как name уникальный по смыслу)
INSERT INTO services (name, description, price)
SELECT 'Breakfast', 'Continental breakfast', 500.00
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name = 'Breakfast');

INSERT INTO services (name, description, price)
SELECT 'Dinner', 'Three-course meal', 1500.00
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name = 'Dinner');

INSERT INTO services (name, description, price)
SELECT 'SPA', 'Spa procedures', 3000.00
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name = 'SPA');

INSERT INTO services (name, description, price)
SELECT 'Laundry', 'Washing and ironing', 800.00
WHERE NOT EXISTS (SELECT 1 FROM services WHERE name = 'Laundry');

-- Вставка комнат (идемпотентно по номеру комнаты)
INSERT INTO rooms (number, category, status, type, capacity, price)
SELECT 101, 'ECONOMY', 'AVAILABLE', 'STANDARD', 2, 2500.00
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE number = 101);

INSERT INTO rooms (number, category, status, type, capacity, price)
SELECT 102, 'ECONOMY', 'AVAILABLE', 'STANDARD', 2, 2500.00
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE number = 102);

INSERT INTO rooms (number, category, status, type, capacity, price)
SELECT 201, 'BUSINESS', 'AVAILABLE', 'DELUXE', 2, 5000.00
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE number = 201);

INSERT INTO rooms (number, category, status, type, capacity, price)
SELECT 202, 'BUSINESS', 'AVAILABLE', 'DELUXE', 2, 5000.00
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE number = 202);

INSERT INTO rooms (number, category, status, type, capacity, price)
SELECT 301, 'PREMIUM', 'AVAILABLE', 'SUITE', 4, 10000.00
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE number = 301);

INSERT INTO rooms (number, category, status, type, capacity, price)
SELECT 302, 'PREMIUM', 'AVAILABLE', 'PRESIDENTIAL', 2, 15000.00
WHERE NOT EXISTS (SELECT 1 FROM rooms WHERE number = 302);