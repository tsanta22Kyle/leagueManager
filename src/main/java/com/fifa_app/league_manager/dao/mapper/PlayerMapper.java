package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.PlayerClubCrudOperations;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerClub;
import com.fifa_app.league_manager.model.Positions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.function.Function;
@Component@RequiredArgsConstructor
public class PlayerMapper implements Function<ResultSet, Player> {

    private final PlayerClubCrudOperations playerClubCrudOperations;

    @SneakyThrows
    @Override
    public Player apply(ResultSet resultSet) {
        Player player = new Player();
        String playerId = resultSet.getString("id");
        List<PlayerClub> playerClubs = playerClubCrudOperations.getPlayerClubsByPlayerId(playerId);
        player.setId(playerId);
        player.setName(resultSet.getString("name"));
        player.setAge(resultSet.getInt("age"));
        player.setCountry(resultSet.getString("country"));
        player.setPosition( Positions.valueOf(resultSet.getObject("position").toString()));
        player.setClubs(playerClubs);
        player.setPreferredNumber(resultSet.getInt("preferred_number"));

        return player;
    }
}
