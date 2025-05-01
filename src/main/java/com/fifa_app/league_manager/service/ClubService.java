package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.ClubOperations;
import com.fifa_app.league_manager.dao.operations.PlayerClubCrudOperations;
import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.endpoint.mapper.CreateOrUpdatePlayerMapper;
import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubOperations clubOperations;
    private final PlayerCrudOperations playerCrudOperations;
    private final CreateOrUpdatePlayerMapper createOrUpdatePlayerMapper;
    private final PlayerClubCrudOperations playerClubCrudOperations;
    public ResponseEntity<Object> getClubs() {
        List<Club> clubs = clubOperations.getAll();
        return ResponseEntity.ok().body(clubs);
    }

    public ResponseEntity<Object> saveAll(List<Club> entities) {
        List<Club> clubs = clubOperations.saveAll(entities);
        return ResponseEntity.ok().body(clubs);
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
        List<Player> newPlayers = playersToSave.stream().map(createOrUpdatePlayer -> createOrUpdatePlayerMapper.toModel(createOrUpdatePlayer)).toList();
        formerPlayers.forEach(player -> {
        //    playerClubCrudOperations.saveAll()
        });
        //  List<Player> players = clubOperations.changePlayers(clubId, entities);
       // return ResponseEntity.ok().body(players);
    }
}
