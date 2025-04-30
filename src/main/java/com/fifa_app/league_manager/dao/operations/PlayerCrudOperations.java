package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import com.fifa_app.league_manager.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository@RequiredArgsConstructor
public class PlayerCrudOperations implements CrudOperations<Player>{

    private final DataSource dataSource;
    private final PlayerMapper playerMapper;
    private final PlayerClubCrudOperations playerClubCrudOperations;



    @Override
    public List<Player> getAll() {

        List<Player> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, name, position, country, age from player ")) {
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

    public Player getById(String playerId){

        Player player = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, name, position, country, age from player where id=?")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            statement.setString(1, playerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    player =  playerMapper.apply(resultSet);

                }
            }
            return player;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Player> saveAll(List<Player> playersToSave){
        List<Player> savedPlayers = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement("INSERT INTO player(id, name, position, country, age) VALUES (?,?,?,?,?) ON CONFLICT (id) " +
                        "DO UPDATE SET name=excluded.name ,position=excluded.position,country=excluded.country,age=excluded.age " +
                        "RETURNING id ,name,position,country,age")
                ){

            playersToSave.forEach(playerToSave -> {
                try{
                    statement.setString(1,playerToSave.getId());
                    statement.setString(2,playerToSave.getName());
                    statement.setObject(3,playerToSave.getPosition().toString(), Types.OTHER);
                    statement.setString(4,playerToSave.getCountry());
                    statement.setInt(5,playerToSave.getAge());
                   // System.out.println("clubs : "+playerToSave.getClubs());
                    playerToSave.getClubs().forEach(playerClub -> playerClub.setPlayer(playerToSave));
                    playerClubCrudOperations.saveAll(playerToSave.getClubs());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            savedPlayers.add(playerMapper.apply(resultSet));
                        }
                    }
                }catch (SQLException e){
                    throw new RuntimeException(e);
                }
            });

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
return savedPlayers;
    }
}
