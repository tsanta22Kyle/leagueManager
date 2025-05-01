CREATE TABLE if not exists player_club
(
    id        varchar primary key,
    player_id varchar,
    club_id   varchar,
    join_date date,
    end_date  date,
    number int  ,
    season_id varchar,
    CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player (id),
    CONSTRAINT club_fk FOREIGN KEY (club_id) REFERENCES club (id),
    unique (number,club_id),
    CONSTRAINT  season_fk FOREIGN KEY (season_id) REFERENCES season(id),
    unique (player_id,season_id)
);