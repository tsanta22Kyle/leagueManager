package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.dao.operations.PlayerMatchCrudOperations;
import com.fifa_app.league_manager.model.PlayerMatch;
import com.fifa_app.league_manager.model.Scoring;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ScoringMapper implements Function<ResultSet, Scoring> {
    private final PlayerMatchCrudOperations playerMatchCrudOperations;

    @Override
    @SneakyThrows
    public Scoring apply(ResultSet resultSet) {
        PlayerMatch playerMatch = playerMatchCrudOperations.getById(resultSet.getString("player_match_id"));

        Scoring scoring = new Scoring();

        scoring.setId(resultSet.getString("id"));
        scoring.setOwnGoal(resultSet.getBoolean("own_goal"));
        scoring.setPlayerMatch(playerMatch);
        scoring.setMinuteOfGoal(resultSet.getInt("minute_of_goal"));

        return scoring;
    }
}
