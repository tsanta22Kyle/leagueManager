package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.ClubOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.PlayerClub;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PlayerClubMapper implements Function<ResultSet, PlayerClub> {

    private final ClubOperations clubOperations;

    @SneakyThrows
    @Override
    public PlayerClub apply(ResultSet resultSet) {

        if (resultSet.getRow() > 0) {
            Date endContractDate = resultSet.getDate("end_date");
            PlayerClub playerClub = new PlayerClub();
            if (endContractDate != null) {
                playerClub.setEndDate(resultSet.getDate("end_date").toLocalDate());
            }

            String clubId = resultSet.getString("club_id");
           // System.out.println("clubId: " + clubId);
            Club club = clubOperations.getById(clubId);
            playerClub.setClub(club);
            playerClub.setId(resultSet.getString("id"));
            playerClub.setJoinDate(resultSet.getDate("join_date").toLocalDate());
            return playerClub;
        }
        return null;
    }
}
