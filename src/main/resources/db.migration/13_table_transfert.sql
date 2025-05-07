DROP TABLE IF EXISTS transfert;

CREATE TYPE transfert_type AS ENUM(
    'IN','OUT'
    );



CREATE TABLE IF NOT EXISTS transfert(
        id varchar primary key ,
        player_id varchar ,
        club_id varchar ,
        type transfert_type  ,
        date_time TIMESTAMP,
        CONSTRAINT player_fk FOREIGN KEY (player_id) REFERENCES player(id),
        CONSTRAINT club_fk FOREIGN KEY (club_id) REFERENCES club(id)

)