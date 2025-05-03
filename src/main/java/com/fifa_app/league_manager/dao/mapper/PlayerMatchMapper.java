package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.GoalCrudOperations;
import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayingTimeCrudOperations;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PlayerMatchMapper implements Function<ResultSet, PlayerMatch> {

    // private final PlayerCrudOperations playerCrudOperations;
    private final MatchCrudOperations matchCrudOperations;
    private final PlayingTimeCrudOperations playingTimeCrudOperations;
    private final GoalCrudOperations goalCrudOperations;

    @Override
    @SneakyThrows
    public PlayerMatch apply(ResultSet resultSet) {
        String playerMatchId = resultSet.getString("id");
        // Player player = playerCrudOperations.getById(resultSet.getString("player_id"));
        Match match = matchCrudOperations.getById(resultSet.getString("match_id"));
        PlayingTime playingTime = playingTimeCrudOperations.getById(resultSet.getString("playing_time_id"));
        List<Goal> goals = goalCrudOperations.getByPlayerMatchId(playerMatchId);

        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setId(playerMatchId);
        // playerMatch.setPlayer(player);
        playerMatch.setMatch(match);
        playerMatch.setPlayingTime(playingTime);
        playerMatch.setGoals(goals);

        return playerMatch;
    }
}
