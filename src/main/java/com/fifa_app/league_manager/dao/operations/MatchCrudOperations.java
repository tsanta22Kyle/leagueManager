package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.MatchMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Match;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchCrudOperations implements CrudOperations<Match> {
    private final MatchMapper matchMapper;
    private final DataSource dataSource;

    @Override
    public List<Match> getAll() {
        return List.of();
    }

    @SneakyThrows
    public Match getById(String matchId) {
        Match match = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select m.id, m.stadium, m.club_playing_home_id, m.club_playing_away_id, m.match_datetime, m.actual_status" +
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
}
