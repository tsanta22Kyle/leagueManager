CREATE TABLE if not exists club_coach
(
    id            varchar      not null primary key,
    team_id          varchar not null,
    coach_id          varchar not null,
    season_id          varchar not null,
    start_date timestamp,
    end_date timestamp
);