CREATE TABLE if not exists club_participation
(
    id       varchar not null primary key,
    club_id  varchar not null references club(id),
    season_id varchar not null references season(id)
);