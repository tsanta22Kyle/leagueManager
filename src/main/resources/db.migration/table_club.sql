CREATE TABLE if not exists club
(
    id            varchar      not null primary key,
    name          varchar(255) not null,
    year_creation bigint check ( length(year_creation) = 4 ),
    acronym       varchar(10),
    stadium       varchar(100) not null
);