CREATE TABLE goal
(
    id       varchar primary key,
    player_match_id varchar not null ,
    own_goal boolean not null ,
    club_match_id varchar,
    season_id varchar,
    minute_of_goal int check ( minute_of_goal <=90 ) not null ,
    CONSTRAINT scorer_fk FOREIGN KEY (player_match_id) REFERENCES player_match(id),
    CONSTRAINT club_fk FOREIGN KEY (club_match_id) REFERENCES club_match(id),
    CONSTRAINT season_fk FOREIGN KEY (season_id) REFERENCES season(id)
)