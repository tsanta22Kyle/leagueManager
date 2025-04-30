CREATE TABLE if not exists coach
(
    id      varchar      not null primary key,
    name    varchar(255) not null unique ,
    country varchar(255) not null
);