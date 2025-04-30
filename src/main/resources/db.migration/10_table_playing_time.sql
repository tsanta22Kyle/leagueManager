CREATE TYPE unit AS ENUM (
    'SECOND','MINUTE','HOUR'
    );

CREATE TABLE if not exists playing_time
(
    id    varchar primary key,
    value int,
    unit  unit
)