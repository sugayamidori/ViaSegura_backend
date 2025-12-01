create table users(
    id uuid not null primary key,
    name varchar(20) not null,
    password varchar(300) not null,
	email varchar(150) not null unique,
    roles varchar[]
);