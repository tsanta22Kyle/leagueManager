package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.TransfertMapper;
import com.fifa_app.league_manager.model.PlayerTransfer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerTransferCrudOperations implements CrudOperations<PlayerTransfer> {
    private final DataSource dataSource;
    private final TransfertMapper transfertMapper;


    @Override@SneakyThrows
    public List<PlayerTransfer> getAll() {
        List<PlayerTransfer> playerTransfers = new ArrayList<>();
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, player_id, club_id, type, date_time FROM transfert")
                ){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    PlayerTransfer playerTransfer = transfertMapper.apply(resultSet);
                    playerTransfers.add(playerTransfer);
                }
            }
        }
        return playerTransfers;
    }
}
