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

    //private final ClubCrudOperations clubCrudOperations;
    private final SeasonCrudOperations seasonCrudOperations;

    @Override
    @SneakyThrows
    public ClubParticipation apply(ResultSet resultSet) {
      //  Club club = clubCrudOperations.getById(resultSet.getString("club_id"));
        Season season = seasonCrudOperations.getById(resultSet.getString("season_id"));

        ClubParticipation clubParticipation = new ClubParticipation();

        clubParticipation.setId(resultSet.getString("id"));
      //  clubParticipation.setClub(club);
        clubParticipation.setSeason(season);

        return clubParticipation;
    }
}
