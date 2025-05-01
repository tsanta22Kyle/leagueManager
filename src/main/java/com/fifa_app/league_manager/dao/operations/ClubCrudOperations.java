package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.ClubMapper;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClubCrudOperations implements CrudOperations<Club> {
    private final DataSource dataSource;
    private final ClubMapper clubMapper;
    private final CoachCrudOperations coachCrudOperations;
    private final ClubCoachCrudOperations clubCoachCrudOperations;

    @Override
    public List<Club> getAll() {
        List<Club> clubs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select c.id, c.name, c.acronym, c.year_creation, c.stadium, cc.coach_id" +
                     " from club c inner join club_coach cc on cc.team_id = c.id;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Club clubFromDb = clubMapper.apply(resultSet);

                    Coach coach = coachCrudOperations.getCoachById(resultSet.getString("coach_id"));

                    clubFromDb.setCoach(coach);
                    clubs.add(clubFromDb);
                }
            }
            return clubs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Club getById(String clubId) {
        Club club = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select c.id, c.name, c.acronym, c.year_creation, c.stadium, cc.coach_id " +
                     "from club c inner join club_coach cc on cc.team_id = c.id WHERE c.id=?")) {
            statement.setString(1, clubId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    //  System.out.println(rs.getString("name"));
                    club = clubMapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return club;
    }

    @SneakyThrows
    public List<Club> saveAll(List<Club> entities) {
        List<Club> clubList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into club (id, name, acronym, year_creation, stadium)"
                     + " values (?, ?, ?, ?, ?) on conflict (name) do update set id=excluded.id ,name=excluded.name,"
                     + " acronym=excluded.acronym, year_creation=excluded.year_creation, stadium=excluded.stadium"
                     + " returning id, name, stadium, year_creation, acronym")) {

            entities.forEach(entityToSave -> {
                try {
                    statement.setString(1, entityToSave.getId());
                    statement.setString(2, entityToSave.getName());
                    statement.setString(3, entityToSave.getAcronym());
                    statement.setLong(4, entityToSave.getYearCreation());
                    statement.setString(5, entityToSave.getStadium());

                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (ResultSet resultSet = statement.executeQuery()) {
                entities.forEach(this::saveCoachAndClubCoach);

                while (resultSet.next()) {
                    Club savedClub = clubMapper.apply(resultSet);

                    ClubCoach clubCoach = clubCoachCrudOperations.findByClubId(savedClub.getId());
                    Coach coach = coachCrudOperations.getCoachById(clubCoach.getCoach().getId());

                    savedClub.setCoach(coach);
                    clubList.add(savedClub);
                }
            }
            return clubList;
        }
    }

    private void saveCoachAndClubCoach(Club entityToSave) {
        Coach coach = coachCrudOperations.save(entityToSave.getCoach());

        ClubCoach clubCoach = new ClubCoach();
        clubCoach.setId(UUID.randomUUID().toString());
        clubCoach.setClub(entityToSave);
        clubCoach.setCoach(coach);

        clubCoachCrudOperations.save(clubCoach);
    }


}
