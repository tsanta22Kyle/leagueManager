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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClubCrudOperations implements CrudOperations<Club> {
    private final DataSource dataSource;
    private final ClubMapper clubMapper;
    private final CoachCrudOperations coachCrudOperations;
    private final ClubCoachCrudOperations clubCoachCrudOperations;

    //private final ClubParticipationCrudOperations clubParticipationCrudOperations;
    // private final ClubMatchCrudOperations clubMatchCrudOperations;
    @Override
    public List<Club> getAll() {
        List<Club> clubs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select c.id, c.name, c.acronym, c.year_creation, c.stadium, cc.coach_id" +
                     " from club c inner join club_coach cc on cc.team_id = c.id order by id;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Club clubFromDb = clubMapper.apply(resultSet);
                    // String clubId = resultSet.getString("id");

                    Coach coach = coachCrudOperations.getCoachById(resultSet.getString("coach_id"));
                    // List<ClubParticipation> clubParticipations = clubParticipationCrudOperations.getManyByClubId(clubId);
                    // clubFromDb.setClubParticipations(clubParticipations);
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
             PreparedStatement statement = conn.prepareStatement("select c.id, c.name, c.acronym, c.year_creation, c.stadium, cc.coach_id  " +
                     "from club c inner join club_coach cc on cc.team_id = c.id WHERE c.id=?")) {
            statement.setString(1, clubId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    club = clubMapper.apply(rs);

                    // List<ClubParticipation> clubParticipations = clubParticipationCrudOperations.getManyByClubId(rs.getString("id"));
                    // club.setClubParticipations(clubParticipations);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return club;
    }

    @SneakyThrows
    public List<Club> saveAll(List<Club> entities) {
        // List<Club> clubList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into club (id, name, acronym, year_creation, stadium)"
                     + " values (?, ?, ?, ?, ?) on conflict (id) do update set name=excluded.name,"
                     + " acronym=excluded.acronym, year_creation=excluded.year_creation, stadium=excluded.stadium")) {

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
            int[] rs = statement.executeBatch();
            if (Arrays.stream(rs).noneMatch(v -> v == 1)) {
                System.out.println("One of entries failed in clubs");
                return null;
            }

            entities.forEach(this::saveCoachAndClubCoach);

            return entities;
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
