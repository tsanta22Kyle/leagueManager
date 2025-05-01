package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.ClubCrudOperations;
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
    private final ClubCrudOperations clubCrudOperations;

    public ResponseEntity<Object> getClubs() {
        List<Club> clubs = clubCrudOperations.getAll();
        return ResponseEntity.ok().body(clubs);
    }

    public ResponseEntity<Object> saveAll(List<Club> entities) {
        List<Club> clubs = clubCrudOperations.saveAll(entities);
        return ResponseEntity.ok().body(clubs);
    }

    public ResponseEntity<Object> getActualPlayers(String clubId) {
        Club existingClub = clubCrudOperations.getById(clubId);
        if (existingClub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, id = " + clubId + " does not exist.");
        }

        List<Player> players = clubCrudOperations.getActualPlayers(clubId);
        return ResponseEntity.ok().body(players);
    }

    public ResponseEntity<Object> changePlayers(String clubId, List<Player> entities) {
        Club existingClub = clubCrudOperations.getById(clubId);
        if (existingClub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, id = " + clubId + " does not exist.");
        }

        List<Player> players = clubCrudOperations.changePlayers(clubId, entities);
        return ResponseEntity.ok().body(players);
    }
}
