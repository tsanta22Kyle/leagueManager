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

import java.time.LocalDate;
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

    public ResponseEntity<Object> saveAll(List<CreateOrUpdatePlayer> players) {
        List<Player> playerToSave = players.stream().map(createOrUpdatePlayer -> {
            Player player = createOrUpdatePlayerMapper.toModel(createOrUpdatePlayer);
            Player existingPlayer = playerCrudOperations.getById(createOrUpdatePlayer.getId());
            if(existingPlayer!=null){
                //System.out.println("existing player clubs are "+existingPlayer.getClubs());
                List<PlayerClub> playerClubs = existingPlayer.getClubs();
            player.setClubs(playerClubs);
            //player.getActualClub()
            return player;
            }
            return player;
        }).toList();
        List<CreateOrUpdatePlayer> savedPlayers = playerCrudOperations.saveAll(playerToSave).stream().map(player -> createOrUpdatePlayerMapper.toRest(player)).toList();

        return ResponseEntity.ok(savedPlayers);
    }
}
