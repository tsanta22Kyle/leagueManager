CREATE TABLE if not exists player_club
(
    id        varchar primary key,
    player_id varchar,
    club_id   varchar,
    join_date date,
    end_date  date,
    number    int,
    season_id varchar,
    CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player (id),
    CONSTRAINT club_fk FOREIGN KEY (club_id) REFERENCES club (id),
    CONSTRAINT season_fk FOREIGN KEY (season_id) REFERENCES season (id)
);
CREATE UNIQUE INDEX unique_number_per_club_active
    ON player_club(number, club_id)
    WHERE end_date IS NULL;
CREATE UNIQUE INDEX unique_active_player_contract
    ON player_club (player_id)
    WHERE end_date IS NULL;
