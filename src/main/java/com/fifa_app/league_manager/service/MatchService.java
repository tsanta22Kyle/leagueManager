package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.*;
import com.fifa_app.league_manager.endpoint.mapper.MatchRestMapper;
import com.fifa_app.league_manager.endpoint.rest.CreateGoal;
import com.fifa_app.league_manager.endpoint.rest.MatchRest;
import com.fifa_app.league_manager.endpoint.rest.UpdateStatus;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final SeasonCrudOperations seasonCrudOperations;
    private final MatchCrudOperations matchCrudOperations;
    private final ClubParticipationCrudOperations clubParticipationCrudOperations;
    private final ClubMatchCrudOperations clubMatchCrudOperations;
    private final MatchRestMapper matchRestMapper;
    private final ClubCrudOperations clubCrudOperations;

    public ResponseEntity<Object> createAllMatches(Year seasonYear) {
        Season season = seasonCrudOperations.getByYear(seasonYear);
        if (season == null) {
            return ResponseEntity.status(NOT_FOUND).body("Season not found");
        }
        if (season.getStatus() != Status.STARTED) {
            return ResponseEntity.badRequest().body("season is not started");
        }

        List<Match> existingMatches = matchCrudOperations.getBySeasonId(season.getId());
        if (!existingMatches.isEmpty()) {
            return ResponseEntity.badRequest().body("matches already exist");
        }

        List<ClubParticipation> participatingClubs = clubParticipationCrudOperations.getBySeasonId(season.getId());
        if (participatingClubs.size() < 2) {
            return ResponseEntity.badRequest().body("Not enough clubs to create matches");
        }

        List<Match> matchesToCreate = new ArrayList<>();
        List<String> matchIds = new ArrayList<>();
        Map<String, Club> homeClubs = new HashMap<>();
        Map<String, Club> awayClubs = new HashMap<>();

        for (int i = 0; i < participatingClubs.size(); i++) {
            for (int j = 0; j < participatingClubs.size(); j++) {
                if (i == j) continue;

                String matchId = UUID.randomUUID().toString();
                Match match = new Match();
                match.setId(matchId);
                match.setSeason(season);
                match.setActualStatus(Status.NOT_STARTED);

                matchesToCreate.add(match);
                matchIds.add(matchId);

                homeClubs.put(matchId, participatingClubs.get(i).getClub());
                awayClubs.put(matchId, participatingClubs.get(j).getClub());
            }
        }

        assignConsecutiveDates(matchesToCreate, Instant.now());
        matchCrudOperations.saveAll(matchesToCreate);

        List<ClubMatch> homeMatchesToSave = new ArrayList<>();
        List<ClubMatch> awayMatchesToSave = new ArrayList<>();
        Map<String, ClubMatch> homeClubMatchMap = new HashMap<>();
        Map<String, ClubMatch> awayClubMatchMap = new HashMap<>();

        for (String matchId : matchIds) {
            // Home
            ClubMatch home = new ClubMatch();
            home.setId(UUID.randomUUID().toString());
            home.setClub(homeClubs.get(matchId));
            home.setMatch(new Match(matchId));
            homeMatchesToSave.add(home);
            homeClubMatchMap.put(matchId, home);

            // Away
            ClubMatch away = new ClubMatch();
            away.setId(UUID.randomUUID().toString());
            away.setClub(awayClubs.get(matchId));
            away.setMatch(new Match(matchId));
            awayMatchesToSave.add(away);
            awayClubMatchMap.put(matchId, away);
        }

        clubMatchCrudOperations.saveAll(homeMatchesToSave);
        clubMatchCrudOperations.saveAll(awayMatchesToSave);

        for (Match match : matchesToCreate) {
            match.setClubPlayingHome(homeClubMatchMap.get(match.getId()));
            match.setClubPlayingAway(awayClubMatchMap.get(match.getId()));
        }

        List<Match> savedMatches = matchCrudOperations.saveAll(matchesToCreate);
        List<MatchRest> restMatches = savedMatches.stream().map(match -> matchRestMapper.toRest(match)).toList();
        return ResponseEntity.ok(restMatches);
    }


    public void assignConsecutiveDates(List<Match> matches, Instant startDate) {
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).setMatchDatetime(startDate.plus(i, ChronoUnit.DAYS));
        }
    }

    public ResponseEntity<Object> getAllSeasonsMatches(Year seasonYear, Status matchStatus, String clubPlayingName, LocalDate matchAfter, LocalDate matchBeforeOrEquals) {

        Season season = seasonCrudOperations.getByYear(seasonYear);

        if (season == null) {
            return ResponseEntity.status(NOT_FOUND).body("Season not found");
        }

        List<MatchRest> matches = matchCrudOperations.getBySeasonId(season.getId()).stream().map(match -> matchRestMapper.toRest(match)).toList();

        List<MatchRest> filteredMatches = matches.stream().filter(match -> match.getActualStatus() == matchStatus)
                .filter(match -> (match.getClubPlayingHome().getName() + match.getClubPlayingAway().getName()).contains(clubPlayingName))
                .filter(matchRest -> LocalDate.ofInstant(matchRest.getMatchDateTime(), ZoneId.systemDefault()).isAfter(matchAfter))
                .filter(matchRest -> (LocalDate.ofInstant(matchRest.getMatchDateTime(), ZoneId.systemDefault()).isBefore(matchBeforeOrEquals) || LocalDate.ofInstant(matchRest.getMatchDateTime(), ZoneId.systemDefault()) == matchBeforeOrEquals)).toList();
        return ResponseEntity.ok(filteredMatches);
    }

    public ResponseEntity<Object> changeMatchStatus(String id, UpdateStatus status) {
        Status matchStatus = status.getStatus();
        Match match = matchCrudOperations.getById(id);
        if (match == null) {
            return ResponseEntity.status(NOT_FOUND).body("Match not found");
        }
        if (match.getActualStatus() == Status.NOT_STARTED && matchStatus == Status.FINISHED) {
            return ResponseEntity.badRequest().body("match is not started yet");
        }
        if(
                match.getActualStatus() == Status.NOT_STARTED && matchStatus == Status.STARTED){
            match.setActualStatus(Status.STARTED);
            Match updatedMatch = matchCrudOperations.saveAll(List.of(match)).get(0);
            MatchRest updatedMatchRest = matchRestMapper.toRest(updatedMatch);


            return ResponseEntity.ok(updatedMatchRest);
        }

        if (match.getActualStatus() == Status.FINISHED) {
            return ResponseEntity.badRequest().body("match is finished");
        }
        if (match.getActualStatus() == Status.STARTED && matchStatus == Status.FINISHED) {

             match.setActualStatus(matchStatus);

            ClubMatch winner = null;
            ClubMatch losing = null;
            if (
                    match.getClubPlayingHome().getScore() > match.getClubPlayingAway().getScore()
            ) {
                winner = match.getClubPlayingHome();
                losing = match.getClubPlayingAway();

                ClubParticipation winnerGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(),winner.getClub().getId());
                winnerGains.setPoints(winnerGains.getPoints()+3);
                winnerGains.setWins(winnerGains.getWins()+1);
                winnerGains.setConcededGoals(winnerGains.getConcededGoals()+losing.getScore());
                winnerGains.setScoredGoals(winnerGains.getScoredGoals()+winner.getScore());
                if(losing.getScore()==0){
                    winnerGains.setCleanSheetNumber(winnerGains.getCleanSheetNumber()+1);
                }
                ClubParticipation losingGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(),losing.getClub().getId());
                losingGains.setConcededGoals(losingGains.getConcededGoals()+winner.getScore());
                losingGains.setScoredGoals(losingGains.getScoredGoals()+losing.getScore());
                losingGains.setLosses(losingGains.getLosses()+1);

            List<ClubParticipation> savedStats =    clubParticipationCrudOperations.saveAll(List.of(winnerGains, losingGains));
                System.out.println("saved stats win home :" + savedStats);

            }if (
                    match.getClubPlayingHome().getScore() < match.getClubPlayingAway().getScore()
            ) {
                losing = match.getClubPlayingHome();
                winner = match.getClubPlayingAway();


                ClubParticipation winnerGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(),winner.getClub().getId());
                winnerGains.setPoints(winnerGains.getPoints()+3);
                winnerGains.setWins(winnerGains.getWins()+1);
                winnerGains.setConcededGoals(winnerGains.getConcededGoals()+losing.getScore());
                winnerGains.setScoredGoals(winnerGains.getScoredGoals()+winner.getScore());
                if(losing.getScore()==0){
                    winnerGains.setCleanSheetNumber(winnerGains.getCleanSheetNumber()+1);
                }
                ClubParticipation losingGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(),losing.getClub().getId());
                losingGains.setConcededGoals(losingGains.getConcededGoals()+winner.getScore());
                losingGains.setScoredGoals(losingGains.getScoredGoals()+losing.getScore());
                losingGains.setLosses(losingGains.getLosses()+1);

                List<ClubParticipation> savedStats =    clubParticipationCrudOperations.saveAll(List.of(winnerGains, losingGains));
                System.out.println("saved stats  lose home:" + savedStats);
            }if(match.getClubPlayingHome().getScore() == match.getClubPlayingAway().getScore()){
                losing = match.getClubPlayingAway();
                winner = match.getClubPlayingHome();

                ClubParticipation winnerGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(),winner.getClub().getId());
                winnerGains.setPoints(winnerGains.getPoints()+1);
                winnerGains.setDraws(winnerGains.getDraws()+1);
                winnerGains.setConcededGoals(winnerGains.getConcededGoals()+losing.getScore());
                winnerGains.setScoredGoals(winnerGains.getScoredGoals()+winner.getScore());
                if(losing.getScore()==0){
                    winnerGains.setCleanSheetNumber(winnerGains.getCleanSheetNumber()+1);
                }
                ClubParticipation losingGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(),losing.getClub().getId());
                losingGains.setPoints(losingGains.getPoints()+1);
                losingGains.setDraws(losingGains.getDraws()+1);
                losingGains.setConcededGoals(losingGains.getConcededGoals()+winner.getScore());
                losingGains.setScoredGoals(losingGains.getScoredGoals()+losing.getScore());
                if(winner.getScore()==0){
                    losingGains.setCleanSheetNumber(losingGains.getCleanSheetNumber()+1);
                }

                winnerGains.setClub(clubCrudOperations.getById(winner.getClub().getId()));
                losingGains.setClub(clubCrudOperations.getById(losing.getClub().getId()));
                List<ClubParticipation> winnerAndLoses = new ArrayList<>();
                winnerAndLoses.add(losingGains);
                winnerAndLoses.add(winnerGains);
                List<ClubParticipation> savedStats = clubParticipationCrudOperations.saveAll(winnerAndLoses);
                System.out.println("saved stats draw  :" + savedStats);
            }
            Match updatedMatch = matchCrudOperations.saveAll(List.of(match)).get(0);
            MatchRest updatedMatchRest = matchRestMapper.toRest(updatedMatch);


            return ResponseEntity.ok(updatedMatchRest);
        }
        return ResponseEntity.internalServerError().body("internal server error");
    }


    public ResponseEntity<Object> addGoals(String id, List<CreateGoal> goals) {

    }
}