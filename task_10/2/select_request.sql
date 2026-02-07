-- 1. найти номер модели, скорость и размер жесткого диска для всех пк стоимостью менее 500 долларов.
select model, speed, hd from Pc where price <= 500;

-- 2.найти производителей принтеров. вывести поля: maker.
select maker from product where type = 'Printer';

-- 3. найти номер модели, объем памяти и размеры экранов ноутбуков, цена которых превышает 1000 долларов.
select model, ram, screen from laptop where price > 1000;

-- 4. найти все записи таблицы printer для цветных принтеров.
select * from printer where color = 'y';

-- 5. найти номер модели, скорость и размер жесткого диска для пк, имеющих скорость cd 12x или 24x и цену менее 600 долларов.
select model, speed, hd from PC where (cd = '12x' or cd = '24x') and price <= 600;

-- 6. указать производителя и скорость для тех ноутбуков, которые имеют жесткий диск объемом не менее 100 гбайт.
select p.maker, l.speed from laptop l join product p on l.model = p.model where l.hd >= 100;

-- 7. найти номера моделей и цены всех продуктов (любого типа), выпущенных производителем b (латинская буква).
select PC.model, PC.price from PC join product p on PC.model = p.model where p.maker = 'b'
union select l.model, l.price from laptop l join product p on l.model = p.model where p.maker = 'b'
union select pr.model, pr.price from printer pr join product p on pr.model = p.model where p.maker = 'b';

-- 8. найти производителя, выпускающего пк, но не ноутбуки.
select maker from product where type = 'PC' except select maker from product where type = 'Laptop';

-- 9. найти производителей пк с процессором не менее 450 мгц. вывести поля: maker.
select distinct p.maker from pc join product p on pc.model = p.model where pc.speed >= 450;

-- 10. найти принтеры, имеющие самую высокую цену. вывести поля: model, price.
select model, price from printer where price = (select max(price) from printer);

-- 11. найти среднюю скорость пк.
select avg(speed) as avg_speed from pc;

-- 12. найти среднюю скорость ноутбуков, цена которых превышает 1000 долларов.
select avg(speed) as avg_speed from laptop where price > 1000;

-- 13. найти среднюю скорость пк, выпущенных производителем a.
select avg(pc.speed) as avg_speed from pc join product p on pc.model = p.model where p.maker = 'a';

-- 14. для каждого значения скорости процессора найти среднюю стоимость пк с такой же скоростью. вывести поля: скорость, средняя цена.
select speed, avg(price) as avg_price from pc group by speed order by speed;

-- 15. найти размеры жестких дисков, совпадающих у двух и более pc. вывести поля: hd.
select hd from pc group by hd having count(*) >= 2;

-- 16. найти пары моделей pc, имеющих одинаковые скорость процессора и ram. в результате каждая пара указывается только один раз, т.е. (i,j), но не (j,i), порядок вывода полей: модель с большим номером, модель с меньшим номером, скорость, ram.
select p1.model as model1, p2.model as model2, p1.speed, p1.ram
from pc p1, pc p2 where p1.speed = p2.speed and p1.ram = p2.ram and p1.model < p2.model
order by p1.model, p2.model;

-- 17. найти модели ноутбуков, скорость которых меньше скорости любого из пк. вывести поля: type, model, speed.
select 'Laptop' as type, l.model, l.speed from laptop l where l.speed < (select min(speed) from pc);

-- 18. найти производителей самых дешевых цветных принтеров. вывести поля: maker, price.
select p.maker, pr.price
from printer pr join product p on pr.model = p.model where pr.color = 'y' and pr.price = (select min(price) from printer where color = 'y');

-- 19. для каждого производителя найти средний размер экрана выпускаемых им ноутбуков. вывести поля: maker, средний размер экрана.
select p.maker, avg(l.screen) as avg_screen
from laptop l join product p on l.model = p.model group by p.maker;

-- 20. найти производителей, выпускающих по меньшей мере три различных модели пк. вывести поля: maker, число моделей.
select p.maker, count(*) as model_count
from pc join product p on pc.model = p.model where p.type = 'PC' group by p.maker having count(*) >= 3;

-- 21. найти максимальную цену пк, выпускаемых каждым производителем. вывести поля: maker, максимальная цена.
select p.maker, max(pc.price) as max_price
from pc join product p on pc.model = p.model group by p.maker;

-- 22. для каждого значения скорости процессора пк, превышающего 600 мгц, найти среднюю цену пк с такой же скоростью. вывести поля: speed, средняя цена.
select speed, avg(price) as avg_price from pc where speed > 600 group by speed order by speed;

-- 23. найти производителей, которые производили бы как пк, так и ноутбуки со скоростью не менее 750 мгц. вывести поля: maker
select distinct p.maker
from product p
join pc on pc.model = p.model and pc.speed >= 750
where p.type = 'PC'
intersect
select distinct p.maker
from product p
join laptop l on l.model = p.model and l.speed >= 750
where p.type = 'Laptop';

-- 24. перечислить номера моделей любых типов, имеющих самую высокую цену по всей имеющейся в базе данных продукции.
select model, price from pc where price = (select max(price) from pc)
union select model, price from laptop where price = (select max(price) from laptop)
union select model, price from printer where price = (select max(price) from printer);

-- 25. найти производителей принтеров, которые производят пк с наименьшим объемом ram и с самым быстрым процессором среди всех пк, имеющих наименьший объем ram. вывести поля: maker
select distinct p.maker
from product p
join pc on pc.model = p.model
where p.type = 'PC' 
  and pc.ram = (select min(ram) from pc)
  and pc.speed = (
    select max(speed) 
    from pc 
    where ram = (select min(ram) from pc)
  )
  and p.maker in (select maker from product where type = 'Printer');