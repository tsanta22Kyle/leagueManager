package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.model.Club;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ClubMapper implements Function<ResultSet, Club> {
    @Override
    @SneakyThrows
    public Club apply(ResultSet resultSet) {
        Club club = new Club();

        club.setId(resultSet.getString("id"));
        club.setName(resultSet.getString("name"));
        club.setYearCreation(resultSet.getLong("year_creation"));
        club.setAcronym(resultSet.getString("acronym"));
        club.setStadium(resultSet.getString("stadium"));

        return club;
    }
}
