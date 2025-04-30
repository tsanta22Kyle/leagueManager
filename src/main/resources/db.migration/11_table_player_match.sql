
CREATE TABLE if not exists player_match
(
    id              varchar primary key,
    player_id       varchar,
    match_id        varchar,
    playing_time_id varchar,
    CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player (id),
    CONSTRAINT match_fk FOREIGN KEY (match_id) REFERENCES match (id),
    CONSTRAINT playing_time_fk FOREIGN KEY (playing_time_id) REFERENCES playing_time (id)
)