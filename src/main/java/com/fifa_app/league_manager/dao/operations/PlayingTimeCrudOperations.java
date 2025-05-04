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
        List<PlayingTime> playingTimes = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into playing_time (id, value, unit)"
                     + " values (?, ?, cast(? as unit)) on conflict (id) do nothing"
                     + " returning id, value, unit")) {

            entities.forEach(entityToSave -> {
                try {
                    statement.setString(1, entityToSave.getId());
                    statement.setInt(2, entityToSave.getValue());
                    statement.setString(3, entityToSave.getUnit().toString());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            playingTimes.add(playingTimeMapper.apply(resultSet));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            return playingTimes;
        }
    }
}
