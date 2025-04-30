CREATE TABLE if not exists player_club (
                            id varchar primary key ,
                            player_id varchar,
                            club_id varchar,
                            join_date date,
                            end_date date,
                            CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player(id) ,
                            CONSTRAINT club_fk  FOREIGN KEY (club_id) REFERENCES club(id)
);