DROP TABLE scoring;

CREATE TABLE if not exists scoring(
    id varchar primary key ,
    player_match_id varchar ,
    own_goal boolean,
    minuteOfGoal int check ( minuteOfGoal <=90 ),
    CONSTRAINT player_match_fk FOREIGN KEY (player_match_id) REFERENCES player_match(id)
)