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
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClubParticipationCrudOperations implements CrudOperations<ClubParticipation> {
    private final DataSource dataSource;
    private final ClubParticipationMapper clubParticipationMapper;
    private final ClubCrudOperations clubCrudOperations;

    @Override
    public List<ClubParticipation> getAll() {
        List<ClubParticipation> clubParticipations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets" +
                     " from club_participation;")) {
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

    public List<ClubParticipation> getManyByClubId(String clubId) {
        List<ClubParticipation> clubParticipations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets" +
                     " from club_participation cp where cp.club_id = ?;")) {

            statement.setString(1, clubId);

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

    public List<ClubParticipation> getBySeasonId(String seasonId) {
        List<ClubParticipation> clubParticipations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets" +
                     " from club_participation cp where cp.season_id = ?;")) {

            statement.setString(1, seasonId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ClubParticipation clubParticipation = clubParticipationMapper.apply(resultSet);
                    clubParticipation.setClub(clubCrudOperations.getById(resultSet.getString("club_id")));

                    clubParticipations.add(clubParticipation);
                }
            }
            return clubParticipations;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<ClubParticipation> saveAll(List<ClubParticipation> entities) {
        List<ClubParticipation> clubParticipations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into club_participation (id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " on conflict (id) do update set conceded_goals=excluded.conceded_goals,scored_goals=excluded.scored_goals,clean_sheets=excluded.clean_sheets,draws=excluded.draws,wins=excluded.wins,losses=excluded.losses,points=excluded.points"
                    + " returning id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets")) {
                entities.forEach(entityToSave -> {
                    try {
                        String id = entityToSave.getId() == null ? UUID.randomUUID().toString() : entityToSave.getId();
                        statement.setString(1, id);
                        statement.setString(2, entityToSave.getClub().getId());
                        statement.setString(3, entityToSave.getSeason().getId());
                        statement.setInt(4, entityToSave.getPoints());
                        statement.setInt(5, entityToSave.getWins());
                        statement.setInt(6, entityToSave.getDraws());
                        statement.setInt(7, entityToSave.getLosses());
                        statement.setInt(8, entityToSave.getScoredGoals());
                        statement.setInt(9, entityToSave.getConcededGoals());
                        statement.setInt(10, entityToSave.getCleanSheetNumber());
                        try (ResultSet resultSet = statement.executeQuery()) {
                            while (resultSet.next()) {
                                clubParticipations.add(clubParticipationMapper.apply(resultSet));
                            }
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                return clubParticipations;
            }
        }
    }

    @SneakyThrows
    public ClubParticipation save(ClubParticipation entity) {
        ClubParticipation clubParticipation = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into club_participation (id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets)"
                    + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " on conflict (id) do update set points=excluded.points, wins=excluded.wins , draws=excluded.draws,losses=excluded.losses,scored_goals=excluded.scored_goals,conceded_goals=excluded.conceded_goals,clean_sheets=excluded.clean_sheets"
                    + " returning id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets")) {
                try {
                    String id = entity.getId() == null ? UUID.randomUUID().toString() : entity.getId();
                    statement.setString(1, id);
                    statement.setString(2, entity.getClub().getId());
                    statement.setString(3, entity.getSeason().getId());
                    statement.setInt(4, entity.getPoints());
                    statement.setInt(5, entity.getWins());
                    statement.setInt(6, entity.getDraws());
                    statement.setInt(7, entity.getLosses());
                    statement.setInt(8, entity.getScoredGoals());
                    statement.setInt(9, entity.getConcededGoals());
                    statement.setInt(10, entity.getCleanSheetNumber());
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

    public ClubParticipation getById(String id) {
        ClubParticipation clubParticipation = null;
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets FROM club_participation where id=?")
                )
        {
            ps.setString(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    clubParticipation = clubParticipationMapper.apply(resultSet);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return clubParticipation;
    }

    public ClubParticipation getBySeasonIdAndClubId(String seasonId, String clubId) {
        ClubParticipation clubParticipation = null;
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT id, club_id, season_id, points, wins, draws, losses, scored_goals, conceded_goals, clean_sheets FROM club_participation where season_id=? and club_id=?")
        )
        {
            ps.setString(1, seasonId);
            ps.setString(2, clubId);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    clubParticipation = clubParticipationMapper.apply(resultSet);
                    clubParticipation.setClub(clubCrudOperations.getById(resultSet.getString("club_id")));
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return clubParticipation;
    }
}
