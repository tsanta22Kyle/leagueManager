package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import com.fifa_app.league_manager.model.Player;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
             PreparedStatement statement = connection.prepareStatement("select id, name, position, country, age ,player.preferred_number from player where id=?")) {
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

    @SneakyThrows
    public List<Player> getActualPlayersByClubId(String clubId) {
        List<Player> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select pl.id, pl.name, pl.country, pl.position, pl.preferred_number, pl.age" +
                     " from player pl inner join player_club plc on plc.club_id = ? WHERE plc.end_date=null")) {
            statement.setString(1, clubId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    players.add(playerMapper.apply(resultSet));
                }
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Player> saveAll(List<Player> playersToSave){
        List<Player> savedPlayers = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement("INSERT INTO player(id, name, position, country, age,preferred_number) VALUES (?,?,?,?,?,?) ON CONFLICT (id) " +
                        "DO UPDATE SET name=excluded.name ,position=excluded.position,country=excluded.country,age=excluded.age ,preferred_number=excluded.preferred_number " +
                        "RETURNING id ,name,position,country,age,preferred_number")
                ){

            playersToSave.forEach(playerToSave -> {
                try{
                    statement.setString(1,playerToSave.getId());
                    statement.setString(2,playerToSave.getName());
                    statement.setObject(3,playerToSave.getPosition().toString(), Types.OTHER);
                    statement.setString(4,playerToSave.getCountry());
                    statement.setInt(5,playerToSave.getAge());
                    statement.setInt(6,playerToSave.getPreferredNumber());
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

    //public List<Player> removePlayersFromClub(){

   // }
}
