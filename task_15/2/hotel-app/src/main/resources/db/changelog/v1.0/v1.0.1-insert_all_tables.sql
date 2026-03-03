INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
VALUES ('employee', 'Ivan', 'Ivanov', 'Ivanovich', '1985-05-15'),
       ('employee', 'Maria', 'Petrova', 'Sergeevna', '1990-08-22'),
       ('employee', 'Alexey', 'Sidorov', 'Vladimirovich', '1988-03-10');

INSERT INTO employees (id, position, hire_date, salary)
VALUES (1, 'ADMIN', '2020-01-15', 50000.00),
       (2, 'MAID', '2021-03-20', 35000.00),
       (3, 'MENDER', '2019-11-10', 45000.00);

INSERT INTO persons (type, name, surname, patronymic, date_of_birth)
VALUES ('client', 'Anna', 'Smirnova', 'Alexandrovna', '1992-07-30'),
       ('client', 'Dmitry', 'Kuznetsov', 'Petrovich', '1987-12-14');

INSERT INTO clients (id)
VALUES (4),
       (5);

INSERT INTO services (name, description, price)
VALUES ('Breakfast', 'Continental breakfast', 500.00),
       ('Dinner', 'Three-course meal', 1500.00),
       ('SPA', 'Spa procedures', 3000.00),
       ('Laundry', 'Washing and ironing', 800.00);

INSERT INTO rooms (number, category, status, type, capacity, price)
VALUES (101, 'ECONOMY', 'AVAILABLE', 'STANDARD', 2, 2500.00),
       (102, 'ECONOMY', 'AVAILABLE', 'STANDARD', 2, 2500.00),
       (201, 'BUSINESS', 'AVAILABLE', 'DELUXE', 2, 5000.00),
       (202, 'BUSINESS', 'AVAILABLE', 'DELUXE', 2, 5000.00),
       (301, 'PREMIUM', 'AVAILABLE', 'SUITE', 4, 10000.00),
       (302, 'PREMIUM', 'AVAILABLE', 'PRESIDENTIAL', 2, 15000.00);