package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.GoalCrudOperations;
import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubMatch;
import com.fifa_app.league_manager.model.Goal;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;
@Component@RequiredArgsConstructor
public class ClubMatchMapper implements Function<ResultSet, ClubMatch> {

    private final MatchCrudOperations matchCrudOperations;
    private final GoalCrudOperations goalCrudOperations;
    private final ClubCrudOperations clubCrudOperations;
    @SneakyThrows
    @Override
    public ClubMatch apply(ResultSet resultSet) {
        ClubMatch clubMatch = new ClubMatch();
        Club club = clubCrudOperations.getById(resultSet.getString("club_id"));
        List<Goal> goals = goalCrudOperations.getByClubMatchId(club.getId());
        clubMatch.setMatch(matchCrudOperations.getById(resultSet.getString("match_id")));
        clubMatch.setId(resultSet.getString("id"));
        clubMatch.setClub(club);
        clubMatch.setGoals(goals);
        return clubMatch;
    }
}
