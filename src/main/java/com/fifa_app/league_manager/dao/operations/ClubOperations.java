package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.ClubMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClubOperations implements CrudOperations<Club> {
    private final DataSource dataSource;
    private final ClubMapper clubMapper;
    private final CoachOperations coachOperations;

    @Override
    public List<Club> getAll() {
        List<Club> clubs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select c.id, c.name, c.acronym, c.year_creation, c.stadium, cc.coach_id from club c inner join club_coach cc on cc.team_id = c.id;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Club clubFromDb = clubMapper.apply(resultSet);

                    Coach coach = coachOperations.getCoachById(resultSet.getString("coach_id"));

                    clubFromDb.setCoach(coach);
                    clubs.add(clubFromDb);
                }
            }
            return clubs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Club getById(String clubId){
        Club club = null;
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement statement = conn.prepareStatement("select c.id, c.name, c.acronym, c.year_creation, c.stadium, cc.coach_id from club c inner join club_coach cc on cc.team_id = c.id where c.id=?")
                )

        {
            statement.setString(1,clubId);
            try(
                    ResultSet rs = statement.executeQuery()
                    ){
                if(rs.next()){

                return clubMapper.apply(rs);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return club;
    }
}
