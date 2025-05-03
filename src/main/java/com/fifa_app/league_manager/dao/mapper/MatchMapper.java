package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.ClubMatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayerMatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MatchMapper implements Function<ResultSet, Match> {
    private final ClubMatchCrudOperations clubMatchCrudOperations;
    // private final PlayerMatchCrudOperations playerMatchCrudOperations;
    private final SeasonCrudOperations seasonCrudOperations;

    @Override
    @SneakyThrows
    public Match apply(ResultSet resultSet) {
        String matchId = resultSet.getString("id");

        Match match = new Match();

        ClubMatch homeClub = clubMatchCrudOperations.getById(resultSet.getString("club_playing_home_id"));
        ClubMatch awayClub = clubMatchCrudOperations.getById(resultSet.getString("club_playing_away_id"));
        //List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByMatchId(matchId);
        Season season = seasonCrudOperations.getById(resultSet.getString("season_id"));

        match.setId(matchId);
        match.setClubPlayingHome(homeClub);
        match.setStadium(match.getClubPlayingHome().getClub().getStadium());
        match.setClubPlayingAway(awayClub);
        match.setMatchDatetime(resultSet.getTimestamp("match_datetime").toInstant());
        match.setActualStatus(Status.valueOf(resultSet.getString("actual_status")));
        //match.setPlayerMatches(playerMatches);
        match.setSeason(season);

        return match;
    }
}
