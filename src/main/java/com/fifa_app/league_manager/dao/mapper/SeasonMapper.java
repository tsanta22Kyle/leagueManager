package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.model.Season;
import com.fifa_app.league_manager.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SeasonMapper implements Function<ResultSet, Season> {
    @Override
    @SneakyThrows
    public Season apply(ResultSet resultSet) {
        Season season = new Season();

        season.setId(resultSet.getString("id"));
        season.setAlias(resultSet.getString("alias"));
        season.setYear(resultSet.getInt("year"));
        season.setStatus(Status.valueOf((String) resultSet.getObject("status")));

        return season;
    }
}
