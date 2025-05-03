package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubParticipation;
import com.fifa_app.league_manager.model.Season;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ClubParticipationMapper implements Function<ResultSet, ClubParticipation> {

    private final SeasonCrudOperations seasonCrudOperations;

    @Override
    @SneakyThrows
    public ClubParticipation apply(ResultSet resultSet) {
        Season season = seasonCrudOperations.getById(resultSet.getString("season_id"));

        ClubParticipation clubParticipation = new ClubParticipation();

        clubParticipation.setId(resultSet.getString("id"));
        clubParticipation.setSeason(season);
        clubParticipation.setDraws(resultSet.getInt("draws"));
        clubParticipation.setWins(resultSet.getInt("wins"));
        clubParticipation.setLosses(resultSet.getInt("losses"));
        clubParticipation.setPoints(resultSet.getInt("points"));
        clubParticipation.setScoredGoals(resultSet.getInt("scored_goals"));
        clubParticipation.setConcededGoals(resultSet.getInt("conceded_goals"));
        clubParticipation.setCleanSheetNumber(resultSet.getInt("clean_sheets"));


        return clubParticipation;
    }
}
