CREATE TABLE player_season(
    id varchar primary key ,
    player_id varchar references player(id),
    season_id varchar references season(id),
    total_playing_time int,
    scored_goals int
)