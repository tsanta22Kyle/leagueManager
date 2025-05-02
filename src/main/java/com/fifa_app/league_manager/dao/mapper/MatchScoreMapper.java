package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.model.Match;
import com.fifa_app.league_manager.model.MatchScore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MatchScoreMapper implements Function<ResultSet, MatchScore> {

    private final MatchCrudOperations matchCrudOperations;

    @Override
    @SneakyThrows
    public MatchScore apply(ResultSet resultSet) {
        Match match = matchCrudOperations.getById(resultSet.getString("match_id"));

        MatchScore matchScore = new MatchScore();

        matchScore.setId(resultSet.getString("id"));
        matchScore.setMatch(match);
        matchScore.setHome(resultSet.getInt("home"));
        matchScore.setAway(resultSet.getInt("away"));

        return matchScore;
    }
}
