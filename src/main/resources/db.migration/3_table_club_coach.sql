CREATE TABLE if not exists club_coach
(
    id         varchar not null primary key,
    team_id    varchar not null references club (id) unique ,
    coach_id   varchar not null references coach (id) unique ,
    start_date timestamp,
    end_date   timestamp
);