package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class GoalMapper implements Function<ResultSet, Goal> {

    @Override
    @SneakyThrows
    public Goal apply(ResultSet resultSet) {
        Goal goal = new Goal();

        goal.setId(resultSet.getString("id"));
        goal.setOwnGoal(resultSet.getBoolean("own_goal"));
        goal.setMinuteOfGoal(resultSet.getInt("minute_of_goal"));
        // goal.setPlayerMatch(scorer);
        // goal.setClubMatch(club);

        return goal;
    }
}
