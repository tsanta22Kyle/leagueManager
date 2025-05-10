package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerStatistics;
import com.fifa_app.league_manager.model.PlayingTime;
import com.fifa_app.league_manager.model.Season;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;


@Component
@RequiredArgsConstructor
public class PlayerStatsMapper implements Function<ResultSet, PlayerStatistics> {
    private final SeasonCrudOperations seasonCrudOperations;
    private final PlayerCrudOperations playerCrudOperations;

    @Override
    @SneakyThrows
    public PlayerStatistics apply(ResultSet resultSet) {

        Season season = seasonCrudOperations.getById(resultSet.getString("season_id"));
        Player player = playerCrudOperations.getById(resultSet.getString("player_id"));

        PlayingTime playingTime = new PlayingTime();
        playingTime.setValue(resultSet.getInt("scored_goals"));

        PlayerStatistics playerStatistics = new PlayerStatistics();

        playerStatistics.setScoredGoals(resultSet.getInt("scored_goals"));
        playerStatistics.setSeason(season);
        playerStatistics.setPlayer(player);

        playerStatistics.setPlayingTime(playingTime);

        return playerStatistics;
    }
}
