package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.*;
import com.fifa_app.league_manager.endpoint.mapper.CreateOrUpdatePlayerMapper;
import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.*;
import com.fifa_app.league_manager.service.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service@RequiredArgsConstructor
public class PlayerService {

    private final PlayerCrudOperations playerCrudOperations;
    private final CreateOrUpdatePlayerMapper createOrUpdatePlayerMapper;
    private final ClubCrudOperations clubCrudOperations;
    private final PlayerClubCrudOperations playerClubCrudOperations;
    private final ClubParticipationCrudOperations clubParticipationCrudOperations;
    private final SeasonCrudOperations seasonCrudOperations;
    private final PlayerMatchCrudOperations playerMatchCrudOperations;
    private final GoalCrudOperations goalCrudOperations;
    private final PlayerSeasonCrudOperation playerSeasonCrudOperation;

    public ResponseEntity<Object> getAllPlayers(String name,int ageMin,int ageMax,String clubName){
        try {
            if(ageMin<0 || ageMax<0){
                throw new ClientException("Please enter a valid age");
            }if(ageMin>ageMax){
                throw new ClientException("minimum age is greater than maximum age");
            }
        List<Player> players = playerCrudOperations.getAll();
            players.forEach(player -> {
                List<PlayerClub> playerClubs = playerClubCrudOperations.getPlayerClubsByPlayerId(player.getId());
                player.setClubs(playerClubs);
            });
            //System.out.println("players: " + players);
        List<Player> filteredPlayers = players.stream()
                .filter(player -> player.getActualClub()!=null)
                .filter(player -> player.getActualClub().getName().toLowerCase().contains(clubName.toLowerCase()))
                .filter(player -> player.getAge()>ageMin).filter(player -> player.getAge()<ageMax)
                .filter(player -> player.getName().toLowerCase().contains(name.toLowerCase())).toList();

        return ResponseEntity.ok(filteredPlayers);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<Object> saveAll(List<CreateOrUpdatePlayer> players) {
        List<Player> playerToSave = players.stream().map(createOrUpdatePlayer -> {
            Player player = createOrUpdatePlayerMapper.toModel(createOrUpdatePlayer);
            Player existingPlayer = playerCrudOperations.getById(createOrUpdatePlayer.getId());
            if(existingPlayer!=null){
                //System.out.println("existing player clubs are "+existingPlayer.getClubs());
                List<PlayerClub> playerClubs = existingPlayer.getClubs();
            player.setClubs(playerClubs);
            return player;
            }
            return player;
        }).toList();
        List<CreateOrUpdatePlayer> savedPlayers = playerCrudOperations.saveAll(playerToSave).stream().map(player -> createOrUpdatePlayerMapper.toRest(player)).toList();

        return ResponseEntity.ok(savedPlayers);
    }

    public  ResponseEntity<Object> getPlayerStatistic(String playerId, Year seasonYear) {
        PlayerStatistics playerStatistics = new PlayerStatistics();

        Player player = playerCrudOperations.getById(playerId);
        List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByPlayerId(playerId);
        List<Goal> playerGoals = new ArrayList<>();
        playerMatches.forEach(playerMatch -> {
            playerGoals.addAll(goalCrudOperations.getByPlayerMatchId(playerMatch.getId()));
        });
        playerGoals.forEach(goal -> {
            goal.setSeason(seasonCrudOperations.getByYear(seasonYear));
        });
        List<Goal> scoredGoals = playerGoals.stream()
                .filter(goal -> goal.getSeason().getYear().equals(seasonYear))
                .filter(goal -> goal.isOwnGoal()==false)
                .toList();

        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found, id = " + playerId + " does not exist.");
        }

        List<Season> seasons = seasonCrudOperations.getAll();
        boolean isProvidedSeasonYearExists = seasons.stream().anyMatch(season -> season.getYear().equals(seasonYear));
        if (!isProvidedSeasonYearExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Season year not found.");
        }

        playerStatistics.setPlayingTime(player.getPlayingTime(seasonYear));
        playerStatistics.setScoredGoals(scoredGoals.size());

        PlayerStatistics finalPlayerStatistics = playerSeasonCrudOperation.getByPlayerIdAndSeasonId(playerId,seasonCrudOperations.getByYear(seasonYear).getId());


        return ResponseEntity.ok(finalPlayerStatistics);
    }
}
