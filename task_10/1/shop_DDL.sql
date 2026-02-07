-- create database shop;
create table Product(
	maker varchar(10) not null,
	model varchar(50) not null primary key,
	type varchar(50) not null check (type in ('PC', 'Laptop', 'Printer'))
);

create table Laptop(
	code int not null primary key,
	model varchar(50) not null,
	speed smallint not null,
	ram smallint not null,
	hd real not null,
	price decimal(10, 2),
	screen smallint not null,
	foreign key (model) references Product(model)
);
create table PC(
	code int not null primary key,
	model varchar(50) not null,
	speed smallint not null,
	ram smallint not null,
	hd real not null,
	cd varchar(10) not null,
	price decimal(10, 2),
	foreign key (model) references Product(model)
);

create table Printer(
	code int not null primary key,
	model varchar(50) not null,
	color char(1) not null check (color in ('y', 'n')), 
	type varchar(10) not null check (type in ('Laser', 'Jet', 'Matrix')),	
	price decimal(10, 2),
	foreign key (model) references Product(model)
);

