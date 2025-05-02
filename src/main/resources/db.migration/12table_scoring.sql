DROP TABLE scoring;

CREATE TABLE if not exists scoring(
    id varchar primary key ,
    player_match_id varchar ,
    own_goal boolean,
    minute_of_goal int check ( minute_of_goal <=90 ),
    CONSTRAINT player_match_fk FOREIGN KEY (player_match_id) REFERENCES player_match(id)
)