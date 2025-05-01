package com.fifa_app.league_manager.dao.mapper;

import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
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

    private final ClubCrudOperations clubOperations;
    //  private final PlayerCrudOperations playerCrudOperations;
    private final SeasonCrudOperations seasonOperations;

    @SneakyThrows
    @Override
    public PlayerClub apply(ResultSet resultSet) {

        if (resultSet.getRow() > 0) {
            Date endContractDate = resultSet.getDate("end_date");
            PlayerClub playerClub = new PlayerClub();
            if (endContractDate != null) {
                playerClub.setEndDate(resultSet.getDate("end_date").toLocalDate());
            }

            String seasonId = resultSet.getString("season_id");
            String clubId = resultSet.getString("club_id");
            Club club = clubOperations.getById(clubId);
            playerClub.setClub(club);
            playerClub.setId(resultSet.getString("id"));
            playerClub.setJoinDate(resultSet.getDate("join_date").toLocalDate());
            playerClub.setNumber(resultSet.getInt("number"));
            playerClub.setSeason(seasonOperations.getById(seasonId));
            return playerClub;
        }
        return null;
    }
}
