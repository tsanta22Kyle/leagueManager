package com.fifa_app.league_manager.endpoint.mapper;


import com.fifa_app.league_manager.model.Coach;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CoachMapper implements Function<ResultSet, Coach> {
    @Override
    @SneakyThrows
    public Coach apply(ResultSet resultSet) {
        Coach coach = new Coach();

        coach.setId(resultSet.getString("id"));
        coach.setName(resultSet.getString("name"));
        coach.setCountry(resultSet.getString("country"));
        return coach;
    }
}
