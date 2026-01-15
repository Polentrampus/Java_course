insert into Product (maker, model, type) values
('a', 'pc001', 'PC'),
('a', 'laptop001', 'Laptop'),
('b', 'pc002', 'PC'),
('b', 'laptop002', 'Laptop'),
('c', 'printer001', 'Printer'),
('c', 'printer002', 'Printer');

insert into PC (code, model, speed, ram, hd, cd, price) values
(1, 'pc001', 3200, 16, 512, '8x', 800),
(2, 'pc002', 3600, 32, 1024, '16x', 1200);

insert into Laptop (code, model, speed, ram, hd, screen, price) values
(1, 'laptop001', 2800, 8, 256, 15, 600),
(2, 'laptop002', 3200, 16, 512, 17, 1000);

insert into Printer (code, model, color, type, price) values
(1, 'printer001', 'y', 'Laser', 300),
(2, 'printer002', 'n', 'Jet', 150);