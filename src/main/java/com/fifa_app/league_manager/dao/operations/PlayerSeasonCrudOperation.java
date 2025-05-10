package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import com.fifa_app.league_manager.model.PlayerStatistics;
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
public class PlayerSeasonCrudOperation implements CrudOperations<PlayerStatistics> {


    public List<PlayerStatistics> saveAll(List<PlayerStatistics> playerStatisticsList) {

    }

    @Override
    @SneakyThrows
    public List<PlayerStatistics> getAll() {
        List<PlayerStatistics> stats = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, season_id, total_playing_time, scored_goals" +
                     " from player_season order by id;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlayerStatistics playerStatistics = playerSeasonMapper.apply(resultSet);
                    stats.add(playerStatistics);
                }
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public PlayerStatistics getByPlayerIdAndSeasonId(String playerId, String seasonId) {
        PlayerStatistics stat = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, season_id, total_playing_time, scored_goals" +
                     " from player_season where player_id = ? and season_id = ? order by id;")) {
            statement.setString(1, playerId);
            statement.setString(2, seasonId);
            /*
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    stat = playerSeasonMapper.apply(resultSet);
                }
            }
            return stat;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerStatistics> getBySeasonId(String seasonId) {
        List<PlayerStatistics> stats = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, season_id, total_playing_time, scored_goals" +
                     " from player_season where season_id = ? order by id;")) {
            statement.setString(1, seasonId);
            /*
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlayerStatistics playerStatistics = playerSeasonMapper.apply(resultSet);
                    stats.add(playerStatistics);
                }
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerStatistics> getByPlayerId(String playerId) {
        List<PlayerStatistics> stats = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, season_id, total_playing_time, scored_goals" +
                     " from player_season where player_id = ? order by id;")) {
            statement.setString(1, playerId);
            /*
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlayerStatistics playerStatistics = playerSeasonMapper.apply(resultSet);
                    stats.add(playerStatistics);
                }
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
