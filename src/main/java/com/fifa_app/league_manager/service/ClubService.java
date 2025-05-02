package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.*;
import com.fifa_app.league_manager.endpoint.mapper.ClubRestMapper;
import com.fifa_app.league_manager.endpoint.mapper.CreateOrUpdatePlayerMapper;
import com.fifa_app.league_manager.endpoint.rest.ClubRest;
import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubCrudOperations clubOperations;
    private final PlayerCrudOperations playerCrudOperations;
    private final CreateOrUpdatePlayerMapper createOrUpdatePlayerMapper;
    private final PlayerClubCrudOperations playerClubCrudOperations;
    private final ClubCrudOperations clubCrudOperations;
    private final ClubParticipationCrudOperations clubParticipationCrudOperations;
    private final SeasonCrudOperations seasonCrudOperations;
    private final ClubRestMapper clubRestMapper;


    public ResponseEntity<Object> getClubs() {
        List<Club> clubs = clubOperations.getAll();
        List<ClubRest> clubRests = clubs.stream().map(clubRestMapper::toRest).toList();
        return ResponseEntity.ok().body(clubRests);
    }

    public ResponseEntity<Object> saveAll(List<Club> entities) {
        List<Club> clubs = clubOperations.saveAll(entities);
        List<ClubRest> clubRests = clubs.stream().map(clubRestMapper::toRest).toList();
        return ResponseEntity.ok().body(clubRests);
    }

    public ResponseEntity<Object> getActualPlayers(String clubId) {
        Club existingClub = clubOperations.getById(clubId);
        if (existingClub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, id = " + clubId + " does not exist.");
        }

        List<Player> players = playerCrudOperations.getActualPlayersByClubId(clubId);
        return ResponseEntity.ok().body(players);
    }

    public ResponseEntity<Object> changePlayers(String clubId, List<CreateOrUpdatePlayer> playersToSave) {
        Club existingClub = clubOperations.getById(clubId);
        if (existingClub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, id = " + clubId + " does not exist.");
        }
        List<Player> formerPlayers = playerCrudOperations.getActualPlayersByClubId(clubId);
        List<Player> newPlayers = playersToSave.stream().map(createOrUpdatePlayerMapper::toModel).toList();
        formerPlayers.forEach(player -> {
        //    playerClubCrudOperations.saveAll()
        });
        //  List<Player> players = clubOperations.changePlayers(clubId, entities);
       // return ResponseEntity.ok().body(players);
        return null;
    }

    public ResponseEntity<Object> attachPlayersToAClub(String clubId, List<Player> entities) {
        List<CreateOrUpdatePlayer> players = new ArrayList<>();

        Club existingClub = clubCrudOperations.getById(clubId);
        if (existingClub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, ID = " + clubId + " does not exist.");
        }
        ClubParticipation existingClubParticipation = clubParticipationCrudOperations.getByClubId(existingClub.getId());

        for (Player player : entities) {
            Player foundPlayer = playerCrudOperations.getById(player.getId());
            Player newPlayer = null;
            if (foundPlayer == null) {
                List<Player> createdPlayers = playerCrudOperations.saveAll(List.of(player));
                newPlayer = createdPlayers.getFirst();
            }

            List<PlayerClub> clubsAttachedToFoundPlayer = playerClubCrudOperations.getPlayerClubsByPlayerId(player.getId());
            if (!clubsAttachedToFoundPlayer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed, player " + player.getName() + " is attached to a club: " + clubsAttachedToFoundPlayer.getFirst().getClub().getName() + ".");
            }

            Season actualSeason = seasonCrudOperations.getById(existingClubParticipation.getSeason().getId());

            PlayerClub playerClub = new PlayerClub();
            playerClub.setId(UUID.randomUUID().toString());
            playerClub.setNumber(newPlayer.getActualNumber());
            playerClub.setSeason(actualSeason);
            playerClub.setClub(existingClub);
            playerClub.setEndDate(null);
            playerClub.setPlayer(newPlayer);
            playerClub.setJoinDate(LocalDate.now());

            List<PlayerClub> clubs = playerClubCrudOperations.saveAll(List.of(playerClub));
            newPlayer.setClubs(clubs);

            players.add(createOrUpdatePlayerMapper.toRest(newPlayer));
        }


        return ResponseEntity.ok().body(players);
    }
}
