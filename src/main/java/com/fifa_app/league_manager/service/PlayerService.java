package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service@RequiredArgsConstructor
public class PlayerService {

    private final PlayerCrudOperations playerCrudOperations;

    public ResponseEntity<Object> getAllPlayers(){
        try {
        List<Player> players = playerCrudOperations.getAll();
        return ResponseEntity.ok(players);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
