package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayingTimeCrudOperations;
import com.fifa_app.league_manager.model.Match;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerMatch;
import com.fifa_app.league_manager.model.PlayingTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PlayerMatchMapper implements Function<ResultSet, PlayerMatch> {

    private final PlayerCrudOperations playerCrudOperations;
    private final MatchCrudOperations matchCrudOperations;
    private final PlayingTimeCrudOperations playingTimeCrudOperations;

    @Override
    @SneakyThrows
    public PlayerMatch apply(ResultSet resultSet) {
        Player player = playerCrudOperations.getById(resultSet.getString("player_id"));
        Match match = matchCrudOperations.getById(resultSet.getString("match_id"));
        PlayingTime playingTime = playingTimeCrudOperations.getById(resultSet.getString("playing_time_id"));

        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setId(resultSet.getString("id"));
        playerMatch.setPlayer(player);
        playerMatch.setMatch(match);
        playerMatch.setPlayingTime(playingTime);

        return playerMatch;
    }
}
