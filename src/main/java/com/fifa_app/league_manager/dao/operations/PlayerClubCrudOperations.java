package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerClubMapper;
import com.fifa_app.league_manager.model.PlayerClub;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Repository@RequiredArgsConstructor
public class PlayerClubCrudOperations {

    private final PlayerClubMapper playerClubMapper;
    private final DataSource dataSource;

    public List<PlayerClub> getPlayerClubsByPlayerId(String playerId) {
        List<PlayerClub> playerClubs = new ArrayList<PlayerClub>();
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("select id, player_id, club_id, join_date, end_date ,player_club.number from player_club where player_id=?");
                ){
            ps.setString(1, playerId);
            try(ResultSet rs = ps.executeQuery();){
                while(rs.next()){
                    playerClubs.add(playerClubMapper.apply(rs));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return playerClubs;
    }


    public List<PlayerClub> saveAll(List<PlayerClub> playerClubs) {
        List<PlayerClub> playerClubSaved = new ArrayList<>();
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement("INSERT INTO player_club(id, player_id, club_id, join_date, end_date, number) VALUES (?,?,?,?,?,?) ON CONFLICT (id) " +
                        "DO UPDATE SET end_date=excluded.end_date,number=excluded.number RETURNING  id,number,end_date,club_id,player_id,join_date")
                )
        {
            playerClubs.forEach(playerClubToSave -> {
                try {
                   // System.out.println("player : "+playerClubToSave.getPlayer());
                    LocalDate endDate = playerClubToSave.getEndDate();
                statement.setString(1,playerClubToSave.getId());
                statement.setString(2,playerClubToSave.getPlayer().getId());
                statement.setString(3,playerClubToSave.getClub().getId());
                statement.setDate(4, Date.valueOf(playerClubToSave.getJoinDate()));
                if(endDate != null){

                statement.setDate(5,Date.valueOf(playerClubToSave.getEndDate()));
                }
                if (endDate == null) {

                    statement.setDate(5,null);
                }
                statement.setInt(6,playerClubToSave.getNumber());

                try(ResultSet rs = statement.executeQuery()){
                    while(rs.next()){
                        playerClubSaved.add(playerClubMapper.apply(rs));
                    }
                }catch (SQLException e){
                    throw new RuntimeException(e);
                }

                }catch (SQLException e){
                    throw new RuntimeException(e);
                }
            });

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return playerClubSaved;
    }

}
