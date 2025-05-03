package com.fifa_app.league_manager.dao.operations;

import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.dao.mapper.ClubMatchMapper;
import com.fifa_app.league_manager.model.ClubMatch;
import com.fifa_app.league_manager.model.Match;
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
public class ClubMatchCrudOperations implements CrudOperations<ClubMatch> {

    private final DataSource dataSource;
    private final ClubMatchMapper clubMatchMapper;
 // private final ClubCrudOperations clubCrudOperations;


    @Override
    public List<ClubMatch> getAll() {
        List<ClubMatch> clubMatches = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select id, club_id, match_id" +
                     " from club_match;")) {
            /*
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * (page - 1));
             */
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ClubMatch clubMatchFromDb = clubMatchMapper.apply(resultSet);
                    clubMatches.add(clubMatchFromDb);
                }
            }
            return clubMatches;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public ClubMatch getById(String id) {
        ClubMatch clubMatch = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, club_id, match_id FROM club_match WHERE id = ?")
        ) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clubMatch = clubMatchMapper.apply(rs);
                //   clubMatch.setClub(clubCrudOperations.getById(rs.getString("club_id")));
                }
            }
        }
        return clubMatch;
    }

    @SneakyThrows
    public List<ClubMatch> getManyByClubId(String clubId) {
        List<ClubMatch> clubMatches = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, club_id, match_id FROM club_match WHERE club_id = ?")
        ) {
            stmt.setString(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clubMatches.add(clubMatchMapper.apply(rs));
                }
            }
        }
        return clubMatches;
    }

    @SneakyThrows
    public List<ClubMatch> saveAll(List<ClubMatch> clubMatchesToSave) {
        List<ClubMatch> savedClubMatches = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO club_match(id,club_id, match_id) VALUES(?,?, ?) on conflict (id) do nothing " +
                     "returning club_id,id,match_id")
        ){

            clubMatchesToSave.forEach(clubMatch -> {
                try {

            stmt.setString(1, clubMatch.getId());
            stmt.setString(2, clubMatch.getClub().getId());
            stmt.setString(3, clubMatch.getMatch().getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                  ClubMatch clubMatchSaved = clubMatchMapper.apply(rs);
                    savedClubMatches.add(clubMatchSaved);
                }
                }
            }catch (SQLException e) {
                    throw new ServerException(e.getMessage());
                }
            });
        }
        return savedClubMatches;
    }
}
