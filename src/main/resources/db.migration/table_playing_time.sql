DROP TABLE playing_time;
CREATE TYPE unit AS ENUM(
    'SECOND','MINUTE','HOUR'
    );

CREATE TABLE playing_time(
    id varchar primary key ,
    value int ,
    unit unit
)