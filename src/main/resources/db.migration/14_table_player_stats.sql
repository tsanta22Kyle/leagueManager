DROP TABLE player_season;

CREATE TABLE player_season(
    player_id varchar references player(id),
    season_id varchar references season(id),
    total_playing_time int,
    scored_goals int,
    primary key (player_id,season_id)
)