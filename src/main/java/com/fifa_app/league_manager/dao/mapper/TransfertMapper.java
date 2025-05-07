package com.fifa_app.league_manager.dao.mapper;


import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerTransfer;
import com.fifa_app.league_manager.model.TransferType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.function.Function;

@Component@RequiredArgsConstructor
public class TransfertMapper implements Function<ResultSet, PlayerTransfer> {

    private final PlayerCrudOperations playerCrudOperations;
    private final ClubCrudOperations clubCrudOperations;

    @Override@SneakyThrows
    public PlayerTransfer apply(ResultSet resultSet) {
        PlayerTransfer playerTransfer = new PlayerTransfer();
        Player player = playerCrudOperations.getById(resultSet.getString("player_id"));
        Club club = clubCrudOperations.getById(resultSet.getString("club_id"));
        playerTransfer.setPlayer(player);
        playerTransfer.setClub(club);
        playerTransfer.setId(resultSet.getString("player_id"));
        playerTransfer.setType( TransferType.valueOf(resultSet.getString("type").toString()));
        playerTransfer.setTransferDate(resultSet.getTimestamp("date_time").toInstant());
        return playerTransfer;
    }
}
