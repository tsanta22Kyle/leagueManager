

do
$$
    begin
        if not exists(select from pg_type where typname = 'match_status') then
            CREATE TYPE match_status AS ENUM(
                'NOT_STARTED' ,'STARTED' ,'FINISHED'
                );
        end if;
    end
$$;

CREATE TABLE if not exists match(
    id varchar primary key ,
    stadium varchar ,
    club_playing_home_id varchar ,
    club_playing_away_id varchar,
    match_datetime timestamp,
    actual_status match_status
)