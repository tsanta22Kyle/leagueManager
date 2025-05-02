package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Match;
import com.fifa_app.league_manager.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MatchMapper implements Function<ResultSet, Match> {
    private final ClubCrudOperations clubCrudOperations;

    @Override
    @SneakyThrows
    public Match apply(ResultSet resultSet) {
        Match match = new Match();

        Club homeClub = clubCrudOperations.getById(resultSet.getString("club_playing_home_id"));
        Club awayClub = clubCrudOperations.getById(resultSet.getString("club_playing_away_id"));

        match.setId(resultSet.getString("id"));
        match.setStadium(resultSet.getString("stadium"));
        match.setClubPlayingHome(homeClub);
        match.setClubPlayingAway(awayClub);
        match.setMatchDatetime(resultSet.getTimestamp("match_datetime").toInstant());
        match.setActualStatus(Status.valueOf(resultSet.getString("actual_status")));

        return match;
    }
}
