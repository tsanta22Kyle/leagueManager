package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerClubMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerClub;
import com.fifa_app.league_manager.service.exceptions.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerClubCrudOperations {

    private final PlayerClubMapper playerClubMapper;
    private final DataSource dataSource;
    private final ClubCrudOperations clubCrudOperations;
    private final PlayerCrudOperations playerCrudOperations;

    public List<PlayerClub> getPlayerClubsByPlayerId(String playerId) {
        List<PlayerClub> playerClubs = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("select id, player_id, club_id, join_date, end_date ,player_club.number,season_id from player_club where player_id=?");
        ) {

            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    PlayerClub playerClub = playerClubMapper.apply(rs);
                    Club club = clubCrudOperations.getById(rs.getString("club_id"));
                    playerClub.setClub(club);

                    playerClubs.add(playerClub);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playerClubs;
    }


    public List<PlayerClub> saveAll(List<PlayerClub> playerClubs) {
        List<PlayerClub> playerClubSaved = new ArrayList<>();
        String sql = "INSERT INTO player_club(id, player_id, club_id, join_date, end_date, number, season_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET end_date = excluded.end_date, season_id = excluded.season_id";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            for (PlayerClub pc : playerClubs) {
                statement.setString(1, pc.getId());
                statement.setString(2, pc.getPlayer().getId());
                statement.setString(3, pc.getClub().getId());
                statement.setDate(4, Date.valueOf(pc.getJoinDate()));
                if (pc.getEndDate() != null) {
                    statement.setDate(5, Date.valueOf(pc.getEndDate()));
                } else {
                    statement.setNull(5, Types.DATE);
                }
                statement.setInt(6, pc.getNumber());
                if (pc.getSeason() != null) {
                    statement.setString(7, pc.getSeason().getId());
                } else {
                    statement.setNull(7, Types.VARCHAR);
                }
                statement.addBatch();
            }

            statement.executeBatch();

            for (PlayerClub pc : playerClubs) {
                Club club = clubCrudOperations.getById(pc.getClub().getId());
                pc.setClub(club);
                playerClubSaved.add(pc);
            }

        } catch (SQLException e) {
            throw new ServerException(e.getMessage());
        }

        return playerClubSaved;
    }


    public List<PlayerClub> getPlayerClubsByClubIdSeasonId(String clubId,String seasonId) {
        List<PlayerClub> playerClubs = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("select id, player_id, club_id, join_date, end_date ,player_club.number,season_id from player_club where club_id=? and season_id=?");
        ) {

            ps.setString(1, clubId);
            ps.setString(2, seasonId);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    PlayerClub playerClub = playerClubMapper.apply(rs);
                    Club club = clubCrudOperations.getById(rs.getString("club_id"));
                    Player player = playerCrudOperations.getById(rs.getString("player_id"));
                    playerClub.setClub(club);
                    playerClub.setPlayer(player);
                    playerClubs.add(playerClub);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playerClubs;
    }
}
