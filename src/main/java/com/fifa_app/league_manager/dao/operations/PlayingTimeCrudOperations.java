package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayingTimeMapper;
import com.fifa_app.league_manager.model.Match;
import com.fifa_app.league_manager.model.PlayerMatch;
import com.fifa_app.league_manager.model.PlayingTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlayingTimeCrudOperations implements CrudOperations<PlayingTime> {
    private final PlayingTimeMapper playingTimeMapper;
    private final DataSource dataSource;

    @Override
    public List<PlayingTime> getAll() {
        return List.of();
    }

    @SneakyThrows
    public PlayingTime getById(String playingTimeId) {
        PlayingTime playingTime = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select plt.id, plt.value, plt.unit" +
                     " from playing_time plt where plt.id = ?;")) {
            statement.setString(1, playingTimeId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    playingTime = playingTimeMapper.apply(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playingTime;
    }

    @SneakyThrows
    public List<PlayingTime> saveAll(List<PlayingTime> entities) {
        List<PlayingTime> saved = new ArrayList<>();

        String sql = "INSERT INTO playing_time (id, value, unit) VALUES (?, ?, cast(? as unit)) " +
                "ON CONFLICT (id) DO UPDATE set value=excluded.value , unit=excluded.unit ";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            for (PlayingTime pt : entities) {
                statement.setString(1, pt.getId());
                statement.setInt(2, pt.getValue());
                statement.setString(3, pt.getUnit().toString());
                statement.addBatch();
            }

            statement.executeBatch();

            List<String> ids = entities.stream().map(PlayingTime::getId).toList();
            if (!ids.isEmpty()) {
                String inClause = ids.stream().map(id -> "?").collect(Collectors.joining(","));
                String selectSql = "SELECT id, value, unit FROM playing_time WHERE id IN (" + inClause + ")";
                try (
                        PreparedStatement selectStatement = connection.prepareStatement(selectSql)
                ) {
                    for (int i = 0; i < ids.size(); i++) {
                        selectStatement.setString(i + 1, ids.get(i));
                    }
                    try (ResultSet rs = selectStatement.executeQuery()) {
                        while (rs.next()) {
                            saved.add(playingTimeMapper.apply(rs));
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return saved;
    }
}
