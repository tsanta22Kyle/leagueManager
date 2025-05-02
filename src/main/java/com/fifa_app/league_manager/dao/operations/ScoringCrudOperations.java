package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.ScoringMapper;
import com.fifa_app.league_manager.model.PlayerMatch;
import com.fifa_app.league_manager.model.Scoring;
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
public class ScoringCrudOperations implements CrudOperations<Scoring> {
    private final DataSource dataSource;
    private final ScoringMapper scoringMapper;


    @Override
    public List<Scoring> getAll() {
        return List.of();
    }

    @SneakyThrows
    public Scoring getById(String scoringId) {
        Scoring scoring = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_match_id, own_goal, minute_of_goal" +
                     " from scoring where id = ?;")) {

            statement.setString(1, scoringId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    scoring = scoringMapper.apply(resultSet);
                }
            }
            return scoring;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Scoring> getManyByPlayerMatchId(String playerMatchId) {
        List<Scoring> scoringList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, player_match_id, own_goal, minute_of_goal" +
                     " from scoring where player_match_id = ?;")) {

            statement.setString(1, playerMatchId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scoringList.add(scoringMapper.apply(resultSet));
                }
            }
            return scoringList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<Scoring> saveAll(List<Scoring> entities) {
        List<Scoring> scoringList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into scoring (id, player_match_id, own_goal, minute_of_goal)"
                     + " values (?, ?, ?, ?) on conflict (id) do update set id=excluded.id"
                     + " returning id, player_match_id, own_goal, minute_of_goal")) {

            entities.forEach(entityToSave -> {
                try {
                    statement.setString(1, entityToSave.getId());
                    statement.setString(2, entityToSave.getPlayerMatch().getId());
                    statement.setBoolean(3, entityToSave.isOwnGoal());
                    statement.setInt(4, entityToSave.getMinuteOfGoal());

                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scoringList.add(scoringMapper.apply(resultSet));
                }
            }
            return scoringList;
        }
    }
}
