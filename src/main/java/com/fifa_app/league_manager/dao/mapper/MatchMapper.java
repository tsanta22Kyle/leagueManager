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
    private final ClubCrudOperations clubCrudOperations;
    // private final PlayerMatchCrudOperations playerMatchCrudOperations;
    private final SeasonCrudOperations seasonCrudOperations;

    @Override
    @SneakyThrows
    public Match apply(ResultSet resultSet) {
        String matchId = resultSet.getString("id");

        Match match = new Match();

        Club homeClub = clubCrudOperations.getById(resultSet.getString("club_playing_home_id"));
        Club awayClub = clubCrudOperations.getById(resultSet.getString("club_playing_away_id"));
        //List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByMatchId(matchId);
        Season season = seasonCrudOperations.getById(homeClub.getSeasonsParticipation().stream()
                .map(ClubParticipation::getSeason).toList()
                .getFirst()
                .getId());

        match.setId(matchId);
        match.setStadium(resultSet.getString("stadium"));
        match.setClubPlayingHome(homeClub);
        match.setClubPlayingAway(awayClub);
        match.setMatchDatetime(resultSet.getTimestamp("match_datetime").toInstant());
        match.setActualStatus(Status.valueOf(resultSet.getString("actual_status")));
        //match.setPlayerMatches(playerMatches);
        match.setSeason(season);

        return match;
    }
}
