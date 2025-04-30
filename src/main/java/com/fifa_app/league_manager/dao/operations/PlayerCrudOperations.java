package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import com.fifa_app.league_manager.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository@RequiredArgsConstructor
public class PlayerCrudOperations implements CrudOperations<Player>{

    private final DataSource dataSource;
    private final PlayerMapper playerMapper;



    @Override
    public List<Player> getAll() {

        List<Player> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, name, number, position, country, age from player ")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                   Player player =  playerMapper.apply(resultSet);
                   players.add(player);
                }
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
