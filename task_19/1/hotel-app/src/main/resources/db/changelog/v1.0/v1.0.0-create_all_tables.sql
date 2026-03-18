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

CREATE TABLE IF NOT EXISTS clients (
    id INTEGER PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    notes TEXT
    );


CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY REFERENCES persons(id) ON DELETE CASCADE,
    position varchar(50) NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(10, 2)
    );

CREATE TABLE IF NOT EXISTS services (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS rooms (
    number INTEGER PRIMARY KEY,
    category varchar(50) NOT NULL,
    status varchar(50) DEFAULT 'AVAILABLE',
    type varchar(50) NOT NULL,
    capacity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL
    );

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

CREATE TABLE IF NOT EXISTS booking_services (
    booking_id INTEGER NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    service_id INTEGER NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    PRIMARY KEY (booking_id, service_id)
    );