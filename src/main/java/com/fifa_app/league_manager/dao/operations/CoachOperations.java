package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.CoachMapper;
import com.fifa_app.league_manager.model.Coach;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CoachOperations implements CrudOperations<Coach> {
    private final DataSource dataSource;
    private final CoachMapper coachMapper;


    @Override
    public List<Coach> getAll() {
        return List.of();
    }

    public Coach getCoachById(String id) {
        Coach coach = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select c.id, c.name, c.country from coach c;")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    coach = coachMapper.apply(resultSet);
                }
            }
            return coach;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
