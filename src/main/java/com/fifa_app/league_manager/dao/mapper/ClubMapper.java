package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.CoachOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ClubMapper implements Function<ResultSet, Club> {

    private final CoachOperations coachOperations;

    @Override
    @SneakyThrows
    public Club apply(ResultSet resultSet) {
        Club club = new Club();
        Coach coach = coachOperations.getCoachById(resultSet.getString("coach_id"));
        club.setId(resultSet.getString("id"));
        club.setName(resultSet.getString("name"));
        club.setYearCreation(resultSet.getLong("year_creation"));
        club.setAcronym(resultSet.getString("acronym"));
        club.setStadium(resultSet.getString("stadium"));
        club.setCoach(coach);


        System.out.println("club"+club);

        return club;
    }
}
