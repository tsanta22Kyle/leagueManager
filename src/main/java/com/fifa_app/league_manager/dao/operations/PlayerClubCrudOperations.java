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
        try (
                Connection conn = dataSource.getConnection();

                PreparedStatement statement = conn.prepareStatement("INSERT INTO player_club(id, player_id, club_id, join_date, end_date, number,season_id) VALUES (?,?,?,?,?,?,?)" +
                        " ON CONFLICT (id) " +
                        "DO UPDATE SET end_date=excluded.end_date,season_id=excluded.season_id RETURNING  id,number,end_date,club_id,player_id,join_date,season_id")
                )
        {
            playerClubs.forEach(playerClubToSave -> {
                try {
                    LocalDate endDate = playerClubToSave.getEndDate();
                statement.setString(1,playerClubToSave.getId());
                statement.setString(2,playerClubToSave.getPlayer().getId());
                statement.setString(3,playerClubToSave.getClub().getId());
                statement.setDate(4, Date.valueOf(playerClubToSave.getJoinDate()));
                if(endDate != null){

                statement.setDate(5,Date.valueOf(playerClubToSave.getEndDate()));
                }
                if (endDate == null) {
                    //System.out.println("end date is null");
                    statement.setDate(5,null);
                }
                statement.setInt(6,playerClubToSave.getNumber());

                       // System.out.println("season id : "+playerClubToSave.getSeason().getId());
                    if(
                            playerClubToSave.getSeason()!=null
                    ){

                        statement.setString(7,playerClubToSave.getSeason().getId());
                    }else{
                        statement.setString(7,null);
                    }
                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        PlayerClub playerClub = playerClubMapper.apply(rs);
                        Club club = clubCrudOperations.getById(rs.getString("club_id"));
                        playerClub.setClub(club);
                        playerClubSaved.add(playerClub);
                    }
                }catch (SQLException e){
                    throw new ServerException(e.getMessage());
                }

                }catch (SQLException e){
                    throw new ServerException(e.getMessage());
                }
            });

        }catch (SQLException e){
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
