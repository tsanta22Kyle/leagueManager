CREATE TABLE if not exists club_participation
(
    id             varchar not null primary key,
    club_id        varchar not null references club (id),
    season_id      varchar not null references season (id),
    points         int,
    wins           int,
    draws          int,
    losses         int,
    scored_goals   int,
    conceded_goals int,
    clean_sheets   int
);