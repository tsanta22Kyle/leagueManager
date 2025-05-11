package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.*;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeasonService {
    private final SeasonCrudOperations seasonCrudOperations;
    private final ClubCrudOperations clubCrudOperations;
    private final ClubParticipationCrudOperations clubParticipationCrudOperations;
    private final PlayerCrudOperations playerCrudOperations;
    private final PlayerSeasonCrudOperation playerSeasonCrudOperation;

    public ResponseEntity<Object> getSeasons() {
        List<Season> seasons = seasonCrudOperations.getAll();
        return ResponseEntity.ok().body(seasons);
    }

    public ResponseEntity<Object> saveAll(List<Season> entities) {
        seasonCrudOperations.saveAll(entities);
        List<Club> clubToAttachToSeason = clubCrudOperations.getAll();
        List<Player> playersToAttachToSeason = playerCrudOperations.getAll();

        List<PlayerStatistics> playerStatisticsListToSave = new ArrayList<>();

        entities.forEach(season -> {
            clubToAttachToSeason.forEach(club -> {
                ClubParticipation clubParticipation = new ClubParticipation();

                clubParticipation.setId(UUID.randomUUID().toString());
                clubParticipation.setSeason(season);
                clubParticipation.setClub(club);
                clubParticipation.setPoints(0);
                clubParticipation.setWins(0);
                clubParticipation.setConcededGoals(0);
                clubParticipation.setScoredGoals(0);
                clubParticipation.setCleanSheetNumber(0);
                clubParticipation.setLosses(0);
                clubParticipation.setDraws(0);
                clubParticipation.setCleanSheetNumber(0);

                clubParticipationCrudOperations.save(clubParticipation);
            });
            playersToAttachToSeason.forEach(player -> {
                PlayerStatistics playerStatistics = new PlayerStatistics();
                playerStatistics.setPlayer(player);
                playerStatistics.setSeason(season);
                playerStatistics.setPlayingTime(new PlayingTime(UUID.randomUUID().toString(),0,DurationUnit.SECOND));
                playerStatistics.setScoredGoals(0);
              //  playerStatisticsListToSave.add(playerStatistics);
                playerSeasonCrudOperation.saveAll(List.of(playerStatistics));
            });
        });
      //  playerSeasonCrudOperation.saveAll(playerStatisticsListToSave);




        return ResponseEntity.ok().body(entities);
    }

    public ResponseEntity<Object> updateStatus(long year, UpdateSeasonStatus entity) {
        List<Season> seasons = seasonCrudOperations.getAll();
        if (
                seasons.stream().anyMatch(season -> season.getStatus().equals(Status.STARTED)) &&
                entity.getStatus().equals(Status.STARTED)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot START this season because another season is still ACTIVE.");
        }

        Season season = seasonCrudOperations.updateStatus(year, entity);
        return ResponseEntity.ok().body(season);
    }

}
