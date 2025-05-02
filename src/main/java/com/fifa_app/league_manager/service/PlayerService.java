package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.PlayerCrudOperations;
import com.fifa_app.league_manager.endpoint.mapper.CreateOrUpdatePlayerMapper;
import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerClub;
import com.fifa_app.league_manager.service.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service@RequiredArgsConstructor
public class PlayerService {

    private final PlayerCrudOperations playerCrudOperations;
    private final CreateOrUpdatePlayerMapper createOrUpdatePlayerMapper;

    public ResponseEntity<Object> getAllPlayers(String name,int ageMin,int ageMax,String clubName){
        try {
            if(ageMin<0 || ageMax<0){
                throw new ClientException("Please enter a valid age");
            }if(ageMin>ageMax){
                throw new ClientException("minimum age is greater than maximum age");
            }
        List<Player> players = playerCrudOperations.getAll();
            System.out.println("players: " + players);
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
}
