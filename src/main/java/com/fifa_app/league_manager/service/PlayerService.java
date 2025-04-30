package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.service.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service@RequiredArgsConstructor
public class PlayerService {

    private final PlayerCrudOperations playerCrudOperations;

    public ResponseEntity<Object> getAllPlayers(String name,int ageMin,int ageMax,String clubName){
        try {
            if(ageMin<0 || ageMax<0){
                throw new ClientException("Please enter a valid age");
            }if(ageMin>ageMax){
                throw new ClientException("minimum age is greater than maximum age");
            }
        List<Player> players = playerCrudOperations.getAll();
        List<Player> filteredPlayers = players.stream()
                .filter(player -> player.getActualClub().getName().toLowerCase().contains(clubName.toLowerCase()))
                .filter(player -> player.getAge()>ageMin).filter(player -> player.getAge()<ageMax)
                .filter(player -> player.getName().toLowerCase().contains(name.toLowerCase())).toList();
        return ResponseEntity.ok(filteredPlayers);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
