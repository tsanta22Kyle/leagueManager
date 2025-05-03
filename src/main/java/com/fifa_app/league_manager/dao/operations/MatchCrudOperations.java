



package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.MatchMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubMatch;
import com.fifa_app.league_manager.model.Coach;
import com.fifa_app.league_manager.model.Match;
import com.fifa_app.league_manager.service.exceptions.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchCrudOperations implements CrudOperations<Match> {
    private final MatchMapper matchMapper;
    private final DataSource dataSource;

    @Override
    public List<Match> getAll() {
        List<Match> matches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, stadium, club_playing_home_id, club_playing_away_id, match_datetime, actual_status, season_id" +
                     " from match;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Match clubFromDb = matchMapper.apply(resultSet);
                    matches.add(clubFromDb);
                }
            }
            return matches;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public Match getById(String matchId) {
        Match match = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select m.id, m.club_playing_home_id, m.club_playing_away_id, m.match_datetime, m.actual_status,season_id" +
                     " from match m where m.id = ?;")) {
            statement.setString(1, matchId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    match = matchMapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return match;
    }

    @SneakyThrows
    public List<Match> saveAll(List<Match> matchesToSave) {
        List<Match> savedMatches = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO match (id, club_playing_home_id, club_playing_away_id, match_datetime, actual_status,season_id) VALUES (?,?,?,?,?,?) ON CONFLICT (id) " +
                     "DO UPDATE SET actual_status=excluded.actual_status , club_playing_away_id=excluded.club_playing_away_id , club_playing_home_id=excluded.club_playing_home_id RETURNING id, club_playing_home_id, club_playing_away_id, match_datetime, actual_status,season_id");
        ) {
            matchesToSave.forEach(matchToSave -> {
                try {
                    statement.setString(1, matchToSave.getId());
                    ClubMatch clubPlayingHome = matchToSave.getClubPlayingHome();
                    ClubMatch clubPlayingAway = matchToSave.getClubPlayingAway();
                    if(clubPlayingAway == null || clubPlayingHome == null) {
                        statement.setString(2,null);
                        statement.setString(3,null);

                    }else if(clubPlayingAway != null && clubPlayingHome !=null){

                        statement.setString(2,matchToSave.getClubPlayingHome().getId());
                        statement.setString(3,matchToSave.getClubPlayingAway().getId());
                    }
                    statement.setTimestamp(4, Timestamp.from(matchToSave.getMatchDatetime()));
                    statement.setObject(5, matchToSave.getActualStatus(),Types.OTHER);
                    statement.setString(6, matchToSave.getSeason().getId());

                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            savedMatches.add(matchMapper.apply(rs));
                        }
                    }
                } catch (SQLException e) {
                    throw new ServerException(e.getMessage());
                }
            });
        }
        return savedMatches;
    }

    public List<Match> getBySeasonId(String id) {
        List<Match> matches = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select id, club_playing_home_id, club_playing_away_id, match_datetime, actual_status, season_id" +
                     " from match m where m.season_id = ?;")) {
            statement.setString(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Match match = matchMapper.apply(rs);
                    matches.add(match);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return matches;
    }
}
