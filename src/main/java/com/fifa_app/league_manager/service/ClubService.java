package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.*;
import com.fifa_app.league_manager.endpoint.mapper.ClubRestMapper;
import com.fifa_app.league_manager.endpoint.mapper.CreateOrUpdatePlayerMapper;
import com.fifa_app.league_manager.endpoint.rest.ClubRest;
import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.model.PlayerClub;
import com.fifa_app.league_manager.model.Status;
import com.fifa_app.league_manager.service.exceptions.ServerException;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        try {


            Club existingClub = clubOperations.getById(clubId);
            if (existingClub == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, id = " + clubId + " does not exist.");
            }
            List<Player> formerPlayers = playerCrudOperations.getActualPlayersByClubId(clubId);
            List<Player> newPlayers = playersToSave.stream().map(createOrUpdatePlayerMapper::toModel).toList();
            List<Player> existingPlayers = new ArrayList<>();
            List<Player> underContractPlayers = new ArrayList<>();
            List<Player> playersToCreate = new ArrayList<>();
            newPlayers.stream().forEach(player -> {
                Player existingPlayer = playerCrudOperations.getById(player.getId());
                if (existingPlayer == null) {
                    playersToCreate.add(player);
                } else {

                    existingPlayers.add(existingPlayer);
                }
            });
            if (!existingPlayers.isEmpty()) {
                underContractPlayers.addAll(
                        existingPlayers.stream()
                                .filter(Objects::nonNull)
                                .filter(player -> player.getActualClub() != null)

                                .filter(player -> player.getActualClub().getActiveSeason() != null)
                                .filter(player -> player.getActualClub().getActiveSeason().getStatus() == Status.STARTED).toList()
                );
            }
            if (underContractPlayers.size() > 0) {
                return ResponseEntity.badRequest().body("there are players under contract : " + underContractPlayers);
            }
            if (
                    existingClub.getActiveSeason() != null && existingClub.getActiveSeason().getStatus() == Status.STARTED
            ) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Season already started");
            } else {
                List<PlayerClub> endContractsToSave = new ArrayList<>();
                existingPlayers.stream()

                        .filter(player -> player.getActualClub() != null)
                        .filter(player -> player != null)
                        .filter(player -> player.getClubs().stream().filter(playerClub -> playerClub.getClub() == existingClub).toList().size() == 0)
                        .filter(player -> player.getActualClub() != existingClub)
                        .forEach(player -> {
                            PlayerClub endContract = new PlayerClub();
                            if (player.getClubs().isEmpty()) {
                                endContract.setClub(existingClub);

                                endContract.setId(UUID.randomUUID().toString());
                            }
                            if (!player.getClubs().isEmpty()) {

                                endContract.setClub(player.getActualClub());
                                endContract.setId(player.endContract().getId());
                            }
                            endContract.setPlayer(player);
                            endContract.setEndDate(LocalDate.now());
                            endContract.setNumber(player.getPreferredNumber());
                            endContract.setSeason(existingClub.getActiveSeason());
                            System.out.println("active season : " + existingClub.getActiveSeason());
                            endContract.setJoinDate(LocalDate.now());

                            playerClubCrudOperations.saveAll(List.of(endContract));
                        });
                formerPlayers.forEach(player -> {

                    PlayerClub endContract = new PlayerClub();
                    if (player.getClubs().isEmpty()) {
                        endContract.setClub(existingClub);

                        endContract.setId(UUID.randomUUID().toString());
                    }
                    if (!player.getClubs().isEmpty()) {

                        endContract.setClub(player.getActualClub());
                        endContract.setId(player.endContract().getId());
                    }
                    endContract.setPlayer(player);
                    endContract.setEndDate(LocalDate.now());
                    endContract.setNumber(player.getPreferredNumber());
                    endContract.setSeason(existingClub.getActiveSeason());
                    endContract.setJoinDate(LocalDate.now());

                    playerClubCrudOperations.saveAll(List.of(endContract));
                });

                List<PlayerClub> newContractsToAdd = new ArrayList<>();
                newPlayers.forEach(player -> {
                    PlayerClub newContract = new PlayerClub();
                    newContract.setPlayer(player);
                    newContract.setId(UUID.randomUUID().toString());
                    newContract.setClub(existingClub);
                    newContract.setSeason(existingClub.getActiveSeason());
                    newContract.setNumber(player.getPreferredNumber());
                    newContract.setEndDate(null);
                    newContract.setJoinDate(LocalDate.now());
                    newContractsToAdd.add(newContract);
                });
                playerCrudOperations.saveAll(playersToCreate);
                // playerClubCrudOperations.saveAll(endContractsToSave);
                playerClubCrudOperations.saveAll(newContractsToAdd);
            }
            //  List<Player> players = clubOperations.changePlayers(clubId, entities);
            List<CreateOrUpdatePlayer> updatedPlayers = playerCrudOperations.saveAll(newPlayers).stream().map(player -> createOrUpdatePlayerMapper.toRest(player)).toList();
            // return ResponseEntity.ok().body(players);
            return ResponseEntity.ok(updatedPlayers);
        } catch (ServerException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<Object> attachPlayersToAClub(String clubId, List<Player> entities) {
        List<CreateOrUpdatePlayer> players = new ArrayList<>();

        Club existingClub = clubCrudOperations.getById(clubId);
        if (existingClub == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Club not found, ID = " + clubId + " does not exist.");
        }
        List<ClubParticipation> existingClubParticipation = clubParticipationCrudOperations.getManyByClubId(existingClub.getId());
        ClubParticipation actualExistingClubParticipation = existingClubParticipation.stream()
                .filter(clubParticipation -> clubParticipation.getSeason().getStatus().equals(Status.STARTED))
                .toList().getFirst();

        for (Player player : entities) {
            Player foundPlayer = playerCrudOperations.getById(player.getId());
            Player newPlayer;

            if (foundPlayer == null) {
                List<Player> createdPlayers = playerCrudOperations.saveAll(List.of(player));
                newPlayer = createdPlayers.getFirst();
            } else newPlayer = foundPlayer;

            List<PlayerClub> clubsAttachedToPlayer = playerClubCrudOperations.getPlayerClubsByPlayerId(newPlayer.getId());
            if (!clubsAttachedToPlayer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed, player " + player.getName() + " is attached to " + clubsAttachedToPlayer.getFirst().getClub().getName() + " club.");
            }

            Season actualSeason = seasonCrudOperations.getById(actualExistingClubParticipation.getSeason().getId());

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
