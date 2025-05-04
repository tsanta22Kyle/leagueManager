package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerMatchMapper;
import com.fifa_app.league_manager.model.PlayerMatch;
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
public class PlayerMatchCrudOperations implements CrudOperations<PlayerMatch> {
    private final DataSource dataSource;
    private final PlayerMatchMapper playerMatchMapper;

    @Override
    public List<PlayerMatch> getAll() {
        return List.of();
    }

    @SneakyThrows
    public PlayerMatch getById(String playerMatchId) {
        PlayerMatch playerMatch = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select pm.id, pm.player_id, pm.match_id, pm.playing_time_id" +
                     " from player_match pm where id = ?;")) {

            statement.setString(1, playerMatchId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    playerMatch = playerMatchMapper.apply(resultSet);
                }
            }
            return playerMatch;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerMatch> getPlayerMatchesByPlayerId(String playerId) {
        List<PlayerMatch> playerMatches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select pm.id, pm.player_id, pm.match_id, pm.playing_time_id" +
                     " from player_match pm where player_id = ?;")) {

            statement.setString(1, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    playerMatches.add(playerMatchMapper.apply(resultSet));
                }
            }
            return playerMatches;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerMatch> getPlayerMatchesByMatchId(String matchId) {
        List<PlayerMatch> playerMatches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select pm.id, pm.player_id, pm.match_id, pm.playing_time_id" +
                     " from player_match pm where match_id = ?;")) {

            statement.setString(1, matchId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    playerMatches.add(playerMatchMapper.apply(resultSet));
                }
            }
            return playerMatches;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerMatch> getPlayerMatchesByPlayerIdAndMatchId(String playerId, String matchId) {
        List<PlayerMatch> playerMatches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select pm.id, pm.player_id, pm.match_id, pm.playing_time_id" +
                     " from player_match pm where match_id = ? and player_id = ?;")) {

            statement.setString(1, matchId);
            statement.setString(2, playerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    playerMatches.add(playerMatchMapper.apply(resultSet));
                }
            }
            return playerMatches;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerMatch> saveAll(List<PlayerMatch> entities) {
        List<PlayerMatch> playerMatches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into player_match (id, player_id, match_id, playing_time_id)"
                     + " values (?, ?, ?, ?) on conflict (id) do update set playing_time_id = excluded.playing_time_id"
                     + " returning id, match_id, player_id, playing_time_id")) {

            entities.forEach(entityToSave -> {
                try {
                    statement.setString(1, entityToSave.getId());
                    statement.setString(2, entityToSave.getPlayer().getId());
                    statement.setString(3, entityToSave.getMatch().getId());
                    statement.setString(4, entityToSave.getPlayingTime().getId());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            playerMatches.add(playerMatchMapper.apply(resultSet));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            return playerMatches;
        }
    }
}
