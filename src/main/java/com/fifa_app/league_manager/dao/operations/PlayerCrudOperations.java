package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerMapper;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerCrudOperations implements CrudOperations<Player> {

    private final DataSource dataSource;
    private final PlayerMapper playerMapper;
   // private final PlayerClubCrudOperations playerClubCrudOperations;
    private final PlayerMatchCrudOperations playerMatchCrudOperations;


    @Override
    public List<Player> getAll() {

        List<Player> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, name, position, country, age,preferred_number from player ")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Player player = playerMapper.apply(resultSet);
                    String playerId = resultSet.getString("id");

                   // List<PlayerClub> playerClubs = playerClubCrudOperations.getPlayerClubsByPlayerId(playerId);
                    List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByPlayerId(playerId);
                  //  player.setClubs(playerClubs);
                    player.setMatches(playerMatches);
                    players.add(player);
                }
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Player getById(String playerId) {

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
                    player = playerMapper.apply(resultSet);
                  //  List<PlayerClub> playerClubs = playerClubCrudOperations.getPlayerClubsByPlayerId(playerId);
                    List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByPlayerId(playerId);
                  //  player.setClubs(playerClubs);
                    player.setMatches(playerMatches);
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
                     " from player pl inner join player_club plc on pl.id = plc.player_id WHERE plc.club_id = ? AND end_date is null")) {
            statement.setString(1, clubId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Player player = playerMapper.apply(resultSet);
                    String playerId = resultSet.getString("id");

                  //  List<PlayerClub> playerClubs = playerClubCrudOperations.getPlayerClubsByPlayerId(playerId);
                    List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByPlayerId(playerId);
                  //  player.setClubs(playerClubs);
                    player.setMatches(playerMatches);
                    players.add(player);
                }
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Player> saveAll(List<Player> playersToSave) {
        List<Player> savedPlayers = new ArrayList<>();

        String sql = "INSERT INTO player(id, name, position, country, age, preferred_number) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = excluded.name, position = excluded.position, " +
                "country = excluded.country, age = excluded.age, preferred_number = excluded.preferred_number";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            for (Player p : playersToSave) {
                statement.setString(1, p.getId());
                statement.setString(2, p.getName());
                statement.setObject(3, p.getPosition().toString(), Types.OTHER);
                statement.setString(4, p.getCountry());
                statement.setInt(5, p.getAge());
                statement.setInt(6, p.getPreferredNumber());

                statement.addBatch();

                // Si le joueur a des PlayerClub liés
                p.getClubs().forEach(pc -> pc.setPlayer(p));
            }

            statement.executeBatch(); // Batch 💥

            // Pas besoin de re-fetch les players si on fait juste du save
            return playersToSave;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
