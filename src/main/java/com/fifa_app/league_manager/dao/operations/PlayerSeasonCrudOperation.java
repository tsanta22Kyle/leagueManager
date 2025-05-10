package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.model.PlayerStatistics;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository@RequiredArgsConstructor
public class PlayerSeasonCrudOperation {

    private final DataSource dataSource;

        @SneakyThrows
        public List<PlayerStatistics> saveAll(List<PlayerStatistics> playerStatisticsList) {
            try(
                    Connection connection = dataSource.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_season ( player_id, season_id, total_playing_time, scored_goals) VALUES (?,?,?,?) " +
                            "ON CONFLICT (season_id,player_id) DO UPDATE SET total_playing_time=excluded.total_playing_time,scored_goals=excluded.scored_goals ")
                    ){
                playerStatisticsList.forEach(playerStatistics -> {
                    try {

                    preparedStatement.setString(1,playerStatistics.getPlayer().getId());
                    preparedStatement.setString(2,playerStatistics.getSeason().getId());
                    preparedStatement.setInt(3,playerStatistics.getPlayingTime().getValue());
                    preparedStatement.setInt(4,playerStatistics.getScoredGoals());
                    preparedStatement.addBatch();
                    }catch (SQLException e){
                        throw new RuntimeException(e);
                    }
                });
                int[] result = preparedStatement.executeBatch();

                if (Arrays.stream(result).allMatch(value -> value!=1)){
                    return List.of();
                }
            }
            return playerStatisticsList;
        }



}
