package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.ClubParticipationMapper;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubCoach;
import com.fifa_app.league_manager.model.ClubParticipation;
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
public class ClubParticipationCrudOperations implements CrudOperations<ClubParticipation> {
    private final DataSource dataSource;
    private final ClubParticipationMapper clubParticipationMapper;

    @Override
    public List<ClubParticipation> getAll() {
        List<ClubParticipation> clubParticipations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select cp.id, cp.club_id, cp.season_id" +
                " from club_participation cp;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    clubParticipations.add(clubParticipationMapper.apply(resultSet));
                }
            }
            return clubParticipations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ClubParticipation getByClubId(String clubId) {
        ClubParticipation clubParticipation = new ClubParticipation();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select cp.id, cp.club_id, cp.season_id" +
                     " from club_participation cp where cp.club_id = ?;")) {

            statement.setString(1, clubId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    clubParticipation = clubParticipationMapper.apply(resultSet);
                }
            }
            return clubParticipation;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ClubParticipation getBySeasonId(String seasonId) {
        ClubParticipation clubParticipation = new ClubParticipation();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select cp.id, cp.club_id, cp.season_id" +
                     " from club_participation cp where cp.season_id = ?;")) {

            statement.setString(1, seasonId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    clubParticipation = clubParticipationMapper.apply(resultSet);
                }
            }
            return clubParticipation;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<ClubParticipation> saveAll(List<ClubParticipation> entities) {
        List<ClubParticipation> clubParticipations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into club_participation (id, club_id, season_id) values (?, ?, ?)"
                    + " on conflict (id) do nothing"
                    + " returning id, club_id, season_id")) {
                entities.forEach(entityToSave -> {
                    try {
                        statement.setString(1, entityToSave.getId());
                        statement.setString(2, entityToSave.getClub().getId());
                        statement.setString(3, entityToSave.getSeason().getId());

                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        clubParticipations.add(clubParticipationMapper.apply(resultSet));
                    }
                }
                return clubParticipations;
            }
        }
    }

    @SneakyThrows
    public ClubParticipation save(ClubParticipation entity) {
        ClubParticipation clubParticipation = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into club_participation (id, club_id, season_id) values (?, ?, ?)"
                    + " on conflict (id) do nothing"
                    + " returning id, club_id, season_id")) {
                    try {
                        statement.setString(1, entity.getId());
                        statement.setString(2, entity.getClub().getId());
                        statement.setString(3, entity.getSeason().getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        clubParticipation = clubParticipationMapper.apply(resultSet);
                    }
                }
                return clubParticipation;
            }
        }
    }
}
