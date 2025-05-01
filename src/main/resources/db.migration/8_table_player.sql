

do
$$
    begin
        if not exists(select from pg_type where typname = 'positions') then
            CREATE TYPE positions AS ENUM (
                'STRIKER', 'MIDFIELDER', 'DEFENSE', 'GOAL_KEEPER'
                );
        end if;
    end
$$;

CREATE TABLE if not exists player(
    id varchar primary key ,
    name varchar(200) ,
    preferred_number int,
    position positions ,
    country varchar ,
    age int
);

