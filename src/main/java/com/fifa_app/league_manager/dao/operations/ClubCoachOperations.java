package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.ClubCoachMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubCoach;
import com.fifa_app.league_manager.model.Coach;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClubCoachOperations implements CrudOperations<ClubCoach> {
    private final DataSource dataSource;
    private final ClubCoachMapper clubCoachMapper;


    @Override
    public List<ClubCoach> getAll() {
        List<ClubCoach> clubs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select c.id, c.coach_id, c.team_id, c.start_date, c.end_date from club_coach c;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clubs.add(clubCoachMapper.apply(resultSet));
                }
            }
            return clubs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ClubCoach findByClubId(String clubId) {
        ClubCoach clubCoach = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select cc.id, cc.coach_id, cc.team_id, cc.start_date, cc.end_date from club_coach cc where team_id = ?;")) {
            statement.setString(1, clubId);
            /*
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clubCoach = clubCoachMapper.apply(resultSet);
                }
            }
            return clubCoach;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public ClubCoach save(ClubCoach entity) {
        ClubCoach clubCoach = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into club_coach (id, team_id, coach_id, start_date, end_date)"
                                 + " values (?, ?, ?, ?, ?)"
                                 + " on conflict (coach_id) do nothing"
                                 + " returning id, team_id, coach_id, start_date, end_date")) {
                try {
                    statement.setString(1, entity.getId());
                    statement.setString(2, entity.getClub().getId());
                    statement.setString(3, entity.getCoach().getId());
                    statement.setTimestamp(4, Timestamp.from(Instant.now()));
                    statement.setTimestamp(5, Timestamp.from(Instant.now()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        clubCoach = clubCoachMapper.apply(resultSet);
                    }
                }
                return clubCoach;
            }
        }
    }
}
