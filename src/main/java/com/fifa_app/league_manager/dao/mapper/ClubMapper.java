package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.ClubParticipationCrudOperations;
import com.fifa_app.league_manager.dao.operations.CoachCrudOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubParticipation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ClubMapper implements Function<ResultSet, Club> {

    private final CoachCrudOperations coachCrudOperations;
    private final ClubParticipationCrudOperations clubParticipationCrudOperations;

    @Override
    @SneakyThrows
    public Club apply(ResultSet resultSet) {
        Club club = new Club();
        String clubId = resultSet.getString("id");
        List<ClubParticipation> clubParticipations = clubParticipationCrudOperations.getManyByClubId(clubId);
        club.setId(clubId);
        club.setName(resultSet.getString("name"));
        club.setYearCreation(resultSet.getLong("year_creation"));
        club.setAcronym(resultSet.getString("acronym"));
        club.setStadium(resultSet.getString("stadium"));
        club.setSeasonsParticipation(clubParticipations);

        return club;
    }
}
