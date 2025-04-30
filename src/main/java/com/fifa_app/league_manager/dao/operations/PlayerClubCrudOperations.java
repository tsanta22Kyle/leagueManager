package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.PlayerClubMapper;
import com.fifa_app.league_manager.model.PlayerClub;
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
public class PlayerClubCrudOperations {

    private final PlayerClubMapper playerClubMapper;
    private final DataSource dataSource;

    public List<PlayerClub> getPlayerClubsByPlayerId(String playerId) {
        List<PlayerClub> playerClubs = new ArrayList<PlayerClub>();
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("select id, player_id, club_id, join_date, end_date from player_club where player_id=?");
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

}
