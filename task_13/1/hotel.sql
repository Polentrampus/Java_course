-- Таблица Person (базовый класс)
CREATE TABLE IF NOT EXISTS persons (
    id SERIAL PRIMARY KEY,
    type VARCHAR(10) CHECK (type IN ('client', 'employee')) NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    patronymic VARCHAR(100),
    date_of_birth DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица Clients (наследник Person)
CREATE TABLE IF NOT EXISTS clients (
    id INTEGER PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    notes TEXT
);

-- Создаем кастомные типы для PostgreSQL
CREATE TYPE position_type AS ENUM ('ADMIN', 'MAID', 'MENDER');
CREATE TYPE room_category_type AS ENUM ('ECONOMY', 'BUSINESS', 'PREMIUM');
CREATE TYPE room_status_type AS ENUM ('AVAILABLE', 'OCCUPIED', 'CLEANING', 'MAINTENANCE');
CREATE TYPE room_type_type AS ENUM ('STANDARD', 'DELUXE', 'SUITE', 'APARTMENT', 'FAMILY', 'PRESIDENTIAL');
CREATE TYPE booking_status_type AS ENUM ('CONFIRMED', 'CANCELLED');
CREATE TYPE event_type_type AS ENUM ('CHECK_IN', 'CHECK_OUT', 'CLEANING_REQUEST', 'MAINTENANCE_REQUEST');

-- Таблица Employees (наследник Person)
CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    position varchar(50) NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(10, 2)
);

-- Таблица Services (услуги)
CREATE TABLE IF NOT EXISTS services (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL
);

-- Таблица Rooms (номера)
CREATE TABLE IF NOT EXISTS rooms (
    number INTEGER PRIMARY KEY,
    category varchar(50) NOT NULL,
    status varchar(50) DEFAULT 'AVAILABLE',
    type varchar(50) NOT NULL,
    capacity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

-- Таблица Bookings (бронирования)
CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    room_number INTEGER NOT NULL REFERENCES rooms(number) ON DELETE CASCADE,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status varchar(20) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица связей Bookings-Services (многие ко многим)
CREATE TABLE IF NOT EXISTS booking_services (
    booking_id INTEGER NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    service_id INTEGER NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    PRIMARY KEY (booking_id, service_id)
);

-- Таблица для уведомлений сотрудников (Observer паттерн)
CREATE TABLE IF NOT EXISTS employee_notifications (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    room_number INTEGER NOT NULL REFERENCES rooms(number) ON DELETE CASCADE,
    event_type event_type_type NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица истории изменений статусов комнат
CREATE TABLE IF NOT EXISTS room_status_history (
    id SERIAL PRIMARY KEY,
    room_number INTEGER NOT NULL REFERENCES rooms(number) ON DELETE CASCADE,
    old_status room_status_type,
    new_status room_status_type NOT NULL,
    changed_by INTEGER REFERENCES employees(id) ON DELETE SET NULL,
    reason TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Индексы для оптимизации
CREATE INDEX idx_person_type ON persons(type);
CREATE INDEX idx_employee_position ON employees(position);
CREATE INDEX idx_room_status ON rooms(status);
CREATE INDEX idx_booking_status ON bookings(status);
CREATE INDEX idx_booking_client ON bookings(client_id);
CREATE INDEX idx_dates ON bookings(check_in_date, check_out_date);
CREATE INDEX idx_employee_read ON employee_notifications(employee_id, is_read);

-- Вставка сотрудников
INSERT INTO persons (type, name, surname, patronymic, date_of_birth) VALUES
('employee', 'Ivan', 'Ivanov', 'Ivanovich', '1985-05-15'),
('employee', 'Maria', 'Petrova', 'Sergeevna', '1990-08-22'),
('employee', 'Alexey', 'Sidorov', 'Vladimirovich', '1988-03-10');

-- Теперь вставляем в employees, зная что ID будут 1, 2, 3 (если таблица была пуста)
INSERT INTO employees (id, position, hire_date, salary) VALUES
(1, 'ADMIN', '2020-01-15', 50000.00),
(2, 'MAID', '2021-03-20', 35000.00),
(3, 'MENDER', '2019-11-10', 45000.00);

-- Вставка клиентов
INSERT INTO persons (type, name, surname, patronymic, date_of_birth) VALUES
('client', 'Anna', 'Smirnova', 'Alexandrovna', '1992-07-30'),
('client', 'Dmitry', 'Kuznetsov', 'Petrovich', '1987-12-14');

INSERT INTO clients (id) VALUES (4), (5);

-- Вставка услуг
INSERT INTO services (name, description, price) VALUES
('Breakfast', 'Continental breakfast', 500.00),
('Dinner', 'Three-course meal', 1500.00),
('SPA', 'Spa procedures', 3000.00),
('Laundry', 'Washing and ironing', 800.00);

-- Вставка номеров
INSERT INTO rooms (number, category, status, type, capacity, price) VALUES
(101, 'ECONOMY', 'AVAILABLE', 'STANDARD', 2, 2500.00),
(102, 'ECONOMY', 'AVAILABLE', 'STANDARD', 2, 2500.00),
(201, 'BUSINESS', 'AVAILABLE', 'DELUXE', 2, 5000.00),
(202, 'BUSINESS', 'AVAILABLE', 'DELUXE', 2, 5000.00),
(301, 'PREMIUM', 'AVAILABLE', 'SUITE', 4, 10000.00),
(302, 'PREMIUM', 'AVAILABLE', 'PRESIDENTIAL', 2, 15000.00);

