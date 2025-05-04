do
$$
    begin
        if not exists(select from pg_type where typname = 'match_status') then
            CREATE TYPE match_status AS ENUM (
                'NOT_STARTED' ,'STARTED' ,'FINISHED'
                );
        end if;
    end
$$;

CREATE TABLE if not exists match
(
    id                   varchar primary key,
    club_playing_home_id varchar,
    club_playing_away_id varchar,
    match_datetime       timestamp,
    actual_status        match_status,
    season_id            varchar,
    CONSTRAINT season_fk
        FOREIGN KEY (season_id)
            REFERENCES season (id)
);

/*
CONSTRAINT check_clubs_differents
        CHECK (club_playing_home_id IS DISTINCT FROM club_playing_away_id)

 */

ALTER TABLE match
    ADD CONSTRAINT fk_home_club
        FOREIGN KEY (club_playing_home_id)
            REFERENCES club_match (id);
ALTER TABLE match
    ADD CONSTRAINT fk_away_club
        FOREIGN KEY (club_playing_away_id)
            REFERENCES club_match (id);