package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.SeasonMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Coach;
import com.fifa_app.league_manager.model.Season;
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
public class SeasonOperations implements CrudOperations<Season> {
    private final DataSource dataSource;
    private final SeasonMapper seasonMapper;

    @Override
    public List<Season> getAll() {
        List<Season> seasons = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select s.id, s.id, s.year, s.alias, s.status from season s;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    seasons.add(seasonMapper.apply(resultSet));
                }
            }
            return seasons;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
