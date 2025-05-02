package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.ClubMatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayerMatchCrudOperations;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class GoalMapper implements Function<ResultSet, Goal> {

    private final PlayerMatchCrudOperations playerMatchCrudOperations;
    private final ClubMatchCrudOperations clubMatchCrudOperations;

    @Override
    @SneakyThrows
    public Goal apply(ResultSet resultSet) {
       // Match match = matchCrudOperations.getById(resultSet.getString("match_id"));
        ClubMatch club = clubMatchCrudOperations.getById(resultSet.getString("club_match_id"));
        PlayerMatch scorer = playerMatchCrudOperations.getById(resultSet.getString("scorer_id"));
        Goal goal = new Goal();
        goal.setId(resultSet.getString("id"));
        goal.setOwnGoal(resultSet.getBoolean("own_goal"));
        goal.setMinuteOfGoal(resultSet.getInt("minute_of_goal"));
        goal.setPlayerMatch(scorer);
        goal.setClubMatch(club);
        return goal;
    }
}
