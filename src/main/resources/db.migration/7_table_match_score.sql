CREATE TABLE match_score
(
    id       varchar primary key,
    home     int,
    away     int,
    match_id varchar,
    CONSTRAINT match_fk FOREIGN KEY (match_id) REFERENCES match (id)
)