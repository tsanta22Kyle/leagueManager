package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerTransfer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerTransferCrudOperations implements CrudOperations<PlayerTransfer> {
    private final DataSource dataSource;
    private final TransferMapper transferMapper;

    @Override
    public List<PlayerTransfer> getAll() {
        return List.of();
    }

    @SneakyThrows
    public List<PlayerTransfer> getById(String id) {
        List<PlayerTransfer> transfers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, club_id, type, date_time"
                     + " from transfert where id = ?;")) {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlayerTransfer transfer = transferMapper.apply(resultSet);
                    transfers.add(transfer);
                }
            }
            return transfers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerTransfer> getsByClubId(String clubId) {
        List<PlayerTransfer> transfers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, club_id, type, date_time"
                     + " from transfert where club_id = ?;")) {
            statement.setString(1, clubId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlayerTransfer transfer = transferMapper.apply(resultSet);
                    transfers.add(transfer);
                }
            }
            return transfers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerTransfer> getsByPlayerId(String plcayerId) {
        List<PlayerTransfer> transfers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_id, club_id, type, date_time"
                     + " from transfert where player_id = ?;")) {
            statement.setString(1, plcayerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PlayerTransfer transfer = transferMapper.apply(resultSet);
                    transfers.add(transfer);
                }
            }
            return transfers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<PlayerTransfer> saveAll(List<PlayerTransfer> entities) {
        // List<PlayerTransfer> playerTransfers = new ArrayList<>();

        String sql = "INSERT INTO transfert(id, player_id, club_id, type, date_time) " +
                "VALUES (?, ?, ?, cast(? as transfert_type), ?) " +
                "ON CONFLICT (id) DO UPDATE SET id=excluded.id, player_id=excluded.player_id, club_id=excluded.club_id, type=excluded.type,"
                + " date_time=excluded.date_time";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            for (PlayerTransfer p : entities) {
                statement.setString(1, p.getId());
                statement.setString(2, p.getPlayer().getId());
                statement.setString(3, p.getClub().getId());
                statement.setString(4, p.getType().name());
                statement.setTimestamp(5, Timestamp.from(p.getTransferDate()));

                statement.addBatch();
            }

            int[] res = statement.executeBatch();
            if (Arrays.stream(res).allMatch(value -> value != 1)) {
                System.out.println("One of entries failed in PlayerTransfer");
                return null;
            }

            return entities;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
