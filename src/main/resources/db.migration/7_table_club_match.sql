CREATE TABLE club_match(
    id varchar primary key ,
    club_id varchar,
    match_id varchar,
    CONSTRAINT club_fk FOREIGN KEY (club_id) REFERENCES club(id),
    CONSTRAINT match_fk FOREIGN KEY (match_id) REFERENCES match(id)
)