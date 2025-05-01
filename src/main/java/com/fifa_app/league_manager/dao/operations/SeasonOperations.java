package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.SeasonMapper;
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

    @SneakyThrows
    public List<Season> saveAll(List<Season> entities) {
        List<Season> seasons = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into season (id, alias, year, status) values (?, ?, ?, cast(? as season_status))"
                    + " on conflict (year) do update set year=excluded.year"
                    + " returning id, alias, year, status")) {
                entities.forEach(entityToSave -> {
                    try {
                        statement.setString(1, UUID.randomUUID().toString());
                        statement.setString(2, entityToSave.getAlias());
                        statement.setLong(3, entityToSave.getYear());
                        statement.setString(4, SeasonStatus.NOT_STARTED.name());

                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        seasons.add(seasonMapper.apply(resultSet));
                    }
                }
                return seasons;
            }
        }
    }

    @SneakyThrows
    public Season updateStatus(long year, UpdateSeasonStatus entity) {
        Season season = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("update season set status = cast(? as season_status) where year = ?"
                    + " returning id, year, status, alias")) {
                try {
                    statement.setString(1, entity.getStatus().name());
                    statement.setLong(2, year);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        season = seasonMapper.apply(resultSet);
                    }
                }
                return season;
            }
        }
    }
}
