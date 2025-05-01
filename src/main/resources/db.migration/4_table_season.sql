
do
$$
    begin
        if not exists(select from pg_type where typname = 'season_status') then
            create type season_status as enum ('NOT_STARTED', 'STARTED', 'FINISHED');
        end if;
    end
$$;


CREATE TABLE IF NOT EXISTS season (
    id varchar not null primary key ,
    year bigint unique,
    alias varchar,
    status season_status
);