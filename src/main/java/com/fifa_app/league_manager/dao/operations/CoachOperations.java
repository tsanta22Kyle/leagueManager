package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.CoachMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    @SneakyThrows
    public List<Coach> saveAll(List<Coach> entities) {
        List<Coach> coaches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into coach (id, name, country) values (?, ?, ?)"
                                 + " on conflict (id, name) do nothing"
                                 + " returning id, name, country")) {
                entities.forEach(entityToSave -> {
                    try {
                        statement.setString(1, entityToSave.getId());
                        statement.setString(2, entityToSave.getName());
                        statement.setString(3, entityToSave.getCountry());

                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        coaches.add(coachMapper.apply(resultSet));
                    }
                }
                return coaches;
            }
        }
    }

    @SneakyThrows
    public Coach save(Coach entity) {
        Coach coach = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement("insert into coach (id, name, country) values (?, ?, ?)"
                                 + " on conflict (name) do update set name=excluded.name"
                                 + " returning id, name, country")) {
                try {
                    statement.setString(1, entity.getId());
                    statement.setString(2, entity.getName());
                    statement.setString(3, entity.getCountry());

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        coach = coachMapper.apply(resultSet);
                    }
                }
            }
            return coach;
        }
    }
}
