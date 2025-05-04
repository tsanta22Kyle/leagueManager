CREATE TABLE club_match(
    id varchar primary key ,
    club_id varchar,
    match_id varchar
);

ALTER TABLE club_match ADD CONSTRAINT club_fk FOREIGN KEY (club_id) REFERENCES club(id);
ALTER TABLE club_match ADD CONSTRAINT match_fk FOREIGN KEY (match_id) REFERENCES match(id);