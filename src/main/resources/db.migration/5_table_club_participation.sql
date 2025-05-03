CREATE TABLE if not exists club_participation
(
    id             varchar not null primary key,
    club_id        varchar not null references club (id),
    season_id      varchar not null references season (id),
    points         int,
    wins           int,
    draws          int,
    losses         int,
    goals_scored   int,
    goals_conceded int,
    clean_sheets   int
);