package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.MatchScoreMapper;
import com.fifa_app.league_manager.model.MatchScore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MatchScoreCrudOperations implements CrudOperations<MatchScore> {
    private final MatchScoreMapper matchScoreMapper;
    private final DataSource dataSource;

    @Override
    public List<MatchScore> getAll() {
        return List.of();
    }

    @SneakyThrows
    public MatchScore getOneByMatchId(String matchId) {
        MatchScore playingTime = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select ms.id, ms.home, ms.away, ms.match_id" +
                     " from match_score ms where ms.match_id = ?;")) {
            statement.setString(1, matchId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    playingTime = matchScoreMapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playingTime;
    }


    @SneakyThrows
    public List<MatchScore> saveAll(List<MatchScore> entities) {
        List<MatchScore> matchScores = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into match_score (id, home, away, match_id)"
                     + " values (?, ?, ?, ?) on conflict (id) do update set id=excluded.id, home=excluded.home, away=excluded.away"
                     + " returning id, home, away, match_id")) {

            entities.forEach(entityToSave -> {
                try {
                    statement.setString(1, entityToSave.getId());
                    statement.setInt(2, entityToSave.getHome());
                    statement.setInt(3, entityToSave.getAway());
                    statement.setString(4, entityToSave.getMatch().getId());

                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    matchScores.add(matchScoreMapper.apply(resultSet));
                }
            }
            return matchScores;
        }
    }
}