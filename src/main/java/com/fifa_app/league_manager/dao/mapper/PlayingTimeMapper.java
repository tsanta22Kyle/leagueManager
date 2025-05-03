package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.model.DurationUnit;
import com.fifa_app.league_manager.model.PlayingTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PlayingTimeMapper implements Function<ResultSet, PlayingTime> {
    @Override
    @SneakyThrows
    public PlayingTime apply(ResultSet resultSet) {
        PlayingTime playingTime = new PlayingTime();

        playingTime.setId(resultSet.getString("id"));
        playingTime.setUnit(DurationUnit.valueOf(resultSet.getString("unit")));
        playingTime.setValue(resultSet.getInt("value"));

        return playingTime;
    }
}
