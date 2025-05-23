package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.GoalMapper;
import com.fifa_app.league_manager.model.Goal;
import com.fifa_app.league_manager.model.Season;
import com.fifa_app.league_manager.service.exceptions.ServerException;
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
public class GoalCrudOperations implements CrudOperations<Goal> {
    private final GoalMapper goalMapper;
    private final DataSource dataSource;
    private final SeasonCrudOperations seasonCrudOperations;


    @Override
    public List<Goal> getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SneakyThrows
    public List<Goal> getByClubMatchId(String clubId) {
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select g.id, g.player_match_id, g.own_goal, g.club_match_id, g.season_id, g.minute_of_goal" +
                     " from goal g where g.club_match_id = ?;")) {
            statement.setString(1, clubId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Goal goal = goalMapper.apply(rs);
                    Season season = seasonCrudOperations.getById(rs.getString("season_id"));

                    goal.setSeason(season);
                    goals.add(goal);
                }
            }
        } catch (SQLException e) {
            throw new ServerException(e.getMessage());
        }
        return goals;
    }

    @SneakyThrows
    public List<Goal> getByPlayerMatchId(String playerMatchId) {
        List<Goal> goals = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement("select id, player_match_id, own_goal, club_match_id, minute_of_goal, season_id" +
                     " from goal g where g.player_match_id = ?;")) {
            statement.setString(1, playerMatchId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Goal goal = goalMapper.apply(rs);

                    Season season = seasonCrudOperations.getById(rs.getString("season_id"));
                    goal.setSeason(season);

                    goals.add(goal);
                }
            }
        } catch (SQLException e) {
            throw new ServerException(e.getMessage());
        }
        return goals;
    }


    @SneakyThrows
    public List<Goal> saveAll(List<Goal> entities) {
        List<Goal> matchScores = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into goal (id, player_match_id, own_goal, club_match_id, minute_of_goal,season_id) VALUES (?,?,?,?,?,?) " +
                     "ON CONFLICT (id) DO UPDATE SET minute_of_goal=excluded.minute_of_goal ,own_goal=excluded.own_goal  " +
                     "RETURNING id, own_goal,club_match_id, minute_of_goal,player_match_id, season_id")) {

            entities.forEach(entityToSave -> {
                try {
                    statement.setString(1, entityToSave.getId());
                    statement.setString(2, entityToSave.getPlayerMatch().getId());
                    statement.setBoolean(3, entityToSave.isOwnGoal());
                    statement.setString(4, entityToSave.getClubMatch().getId());
                    statement.setInt(5, entityToSave.getMinuteOfGoal());
                    statement.setString(6,entityToSave.getSeason().getId());

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Goal goal = goalMapper.apply(resultSet);
                            goal.setSeason(seasonCrudOperations.getById(resultSet.getString("season_id")));
                            matchScores.add(goal);
                        }
                    }
                } catch (SQLException e) {
                    throw new ServerException(e.getMessage());
                }
            });
            return matchScores;
        }
    }
}