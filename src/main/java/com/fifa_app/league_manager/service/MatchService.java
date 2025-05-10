package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.*;
import com.fifa_app.league_manager.endpoint.mapper.MatchRestMapper;
import com.fifa_app.league_manager.endpoint.mapper.ScorerRestMapper;
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
    private final PlayerMatchCrudOperations playerMatchCrudOperations;
    private final GoalCrudOperations goalCrudOperations;
    private final PlayerCrudOperations playerCrudOperations;
    private final PlayerClubCrudOperations playerClubCrudOperations;
    private final PlayingTimeCrudOperations playingTimeCrudOperations;
    private final ScorerRestMapper scorerRestMapper;

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

                homeClubs.put(matchId, participatingClubs.get(i).getClub());
                awayClubs.put(matchId, participatingClubs.get(j).getClub());
            }
        }

        assignConsecutiveDates(matchesToCreate, Instant.now());
        matchCrudOperations.saveAll(matchesToCreate);

        List<ClubMatch> homeMatches = new ArrayList<>();
        List<ClubMatch> awayMatches = new ArrayList<>();
        Map<String, ClubMatch> homeMap = new HashMap<>();
        Map<String, ClubMatch> awayMap = new HashMap<>();

        for (Match match : matchesToCreate) {
            String matchId = match.getId();

            ClubMatch home = new ClubMatch(UUID.randomUUID().toString(), homeClubs.get(matchId), match);
            ClubMatch away = new ClubMatch(UUID.randomUUID().toString(), awayClubs.get(matchId), match);

            homeMatches.add(home);
            awayMatches.add(away);
            homeMap.put(matchId, home);
            awayMap.put(matchId, away);
        }

        clubMatchCrudOperations.saveAll(homeMatches);
        clubMatchCrudOperations.saveAll(awayMatches);

        for (Match match : matchesToCreate) {
            match.setClubPlayingHome(homeMap.get(match.getId()));
            match.setClubPlayingAway(awayMap.get(match.getId()));
        }

        List<Match> updatedMatches = matchCrudOperations.saveAll(matchesToCreate);
        List<MatchRest> restMatches = updatedMatches.stream().map(matchRestMapper::toRest).toList();

        // ✅ On charge les joueurs club par club, une fois, et on stocke ça
        Map<String, List<PlayerClub>> playerClubsByClubId = new HashMap<>();
        for (ClubParticipation cp : participatingClubs) {
            String clubId = cp.getClub().getId();
            List<PlayerClub> players = playerClubCrudOperations.getPlayerClubsByClubIdSeasonId(clubId, season.getId());
            playerClubsByClubId.put(clubId, players);
        }

        List<PlayerMatch> allPlayerMatches = new ArrayList<>();
        List<PlayingTime> allPlayingTimes = new ArrayList<>();

        for (Match match : updatedMatches) {
            String homeClubId = match.getClubPlayingHome().getClub().getId();
            String awayClubId = match.getClubPlayingAway().getClub().getId();

            List<PlayerClub> homePlayers = playerClubsByClubId.getOrDefault(homeClubId, List.of());
            List<PlayerClub> awayPlayers = playerClubsByClubId.getOrDefault(awayClubId, List.of());

            for (PlayerClub playerClub : homePlayers) {
                PlayingTime pt = new PlayingTime(UUID.randomUUID().toString(), 0, DurationUnit.MINUTE);
                PlayerMatch pm = new PlayerMatch(UUID.randomUUID().toString(), playerClub.getPlayer(), match, pt);
                allPlayingTimes.add(pt);
                allPlayerMatches.add(pm);
            }

            for (PlayerClub playerClub : awayPlayers) {
                PlayingTime pt = new PlayingTime(UUID.randomUUID().toString(), 0, DurationUnit.MINUTE);
                PlayerMatch pm = new PlayerMatch(UUID.randomUUID().toString(), playerClub.getPlayer(), match, pt);
                allPlayingTimes.add(pt);
                allPlayerMatches.add(pm);
            }
        }

        playingTimeCrudOperations.saveAll(allPlayingTimes);
        playerMatchCrudOperations.saveAll(allPlayerMatches);

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

        List<Match> matchList = matchCrudOperations.getBySeasonId(season.getId());
        matchList.forEach(match -> {
            List<PlayerMatch> playerMatchList = playerMatchCrudOperations.getPlayerMatchesByMatchId(match.getId());
            playerMatchList.forEach(playerMatch -> {
                List<Goal> goals = goalCrudOperations.getByPlayerMatchId(playerMatch.getId());
            match.getClubPlayingHome().getGoals().forEach(goal -> {
             if(goals.contains(goal)){
                 goal.setPlayerMatch(playerMatch);
             }
            });
            match.getClubPlayingAway().getGoals().forEach(goal -> {
             if(goals.contains(goal)){
                 goal.setPlayerMatch(playerMatch);
             }
            });
            });
        });

        List<MatchRest> matches = matchList.stream().map(match -> matchRestMapper.toRest(match)).toList();

        List<MatchRest> filteredMatches = matches.stream().filter(match -> match.getActualStatus() == matchStatus)
                .filter(match -> (match.getClubPlayingHome().getName() + match.getClubPlayingAway().getName()).contains(clubPlayingName))
                .filter(matchRest -> LocalDate.ofInstant(matchRest.getMatchDateTime(), ZoneId.systemDefault()).isAfter(matchAfter))
                .filter(matchRest -> (LocalDate.ofInstant(matchRest.getMatchDateTime(), ZoneId.systemDefault()).isBefore(matchBeforeOrEquals) || LocalDate.ofInstant(matchRest.getMatchDateTime(), ZoneId.systemDefault()) == matchBeforeOrEquals)).toList();
        return ResponseEntity.ok(filteredMatches);
    }

    public ResponseEntity<Object> changeMatchStatus(String id, UpdateStatus status) {
        Status matchStatus = status.getStatus();
        Match match = matchCrudOperations.getById(id);
        System.out.println("goals  " + match.getClubPlayingHome().getClub().getId());
        String homeClubId = match.getClubPlayingHome().getClub().getId();
        String matchId = match.getId();


        if (match == null) {
            return ResponseEntity.status(NOT_FOUND).body("Match not found");
        }
        if (match.getActualStatus() == Status.NOT_STARTED && matchStatus == Status.FINISHED) {
            return ResponseEntity.badRequest().body("match is not started yet");
        }
        if (
                match.getActualStatus() == Status.NOT_STARTED && matchStatus == Status.STARTED) {
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

                ClubParticipation winnerGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(), winner.getClub().getId());
                winnerGains.setPoints(winnerGains.getPoints() + 3);
                winnerGains.setWins(winnerGains.getWins() + 1);
                winnerGains.setConcededGoals(winnerGains.getConcededGoals() + losing.getScore());

                List<Goal> winnerGoals = goalCrudOperations.getByClubMatchId(winner.getId());

              //  winnerGains.setScoredGoals(winnerGains.getScoredGoals() + winner.getScore());
                winnerGains.setScoredGoals(winnerGoals.size() );

                winnerGains.setClub(winner.getClub());
                if (losing.getScore() == 0) {
                    winnerGains.setCleanSheetNumber(winnerGains.getCleanSheetNumber() + 1);
                }
                ClubParticipation losingGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(), losing.getClub().getId());
                losingGains.setConcededGoals(losingGains.getConcededGoals() + winner.getScore());
                List<Goal> loserGoals = goalCrudOperations.getByClubMatchId(losing.getId());
                losingGains.setScoredGoals(loserGoals.size());
                losingGains.setLosses(losingGains.getLosses() + 1);
                losingGains.setClub(losing.getClub());
                List<ClubParticipation> savedStats = clubParticipationCrudOperations.saveAll(List.of(winnerGains, losingGains));
                System.out.println("saved stats win home :" + savedStats);

            }
            if (
                    match.getClubPlayingHome().getScore() < match.getClubPlayingAway().getScore()
            ) {
                losing = match.getClubPlayingHome();
                winner = match.getClubPlayingAway();


                ClubParticipation winnerGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(), winner.getClub().getId());
                winnerGains.setPoints(winnerGains.getPoints() + 3);
                winnerGains.setWins(winnerGains.getWins() + 1);
                winnerGains.setConcededGoals(winnerGains.getConcededGoals() + losing.getScore());

                List<Goal> winnerGoals = goalCrudOperations.getByClubMatchId(winner.getId());

                //  winnerGains.setScoredGoals(winnerGains.getScoredGoals() + winner.getScore());
                winnerGains.setScoredGoals(winnerGoals.size() );

                winnerGains.setClub(winner.getClub());
                if (losing.getScore() == 0) {
                    winnerGains.setCleanSheetNumber(winnerGains.getCleanSheetNumber() + 1);
                }
                ClubParticipation losingGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(), losing.getClub().getId());
                losingGains.setConcededGoals(losingGains.getConcededGoals() + winner.getScore());
                List<Goal> loserGoals = goalCrudOperations.getByClubMatchId(losing.getId());
                losingGains.setScoredGoals(loserGoals.size());
                // losingGains.setScoredGoals(losingGains.getScoredGoals() + losing.getScore());
                losingGains.setLosses(losingGains.getLosses() + 1);
                losingGains.setClub(losing.getClub());
                List<ClubParticipation> savedStats = clubParticipationCrudOperations.saveAll(List.of(winnerGains, losingGains));
                System.out.println("saved stats  lose home:" + savedStats);
            }
            if (match.getClubPlayingHome().getScore() == match.getClubPlayingAway().getScore()) {
                losing = match.getClubPlayingAway();
                winner = match.getClubPlayingHome();

                ClubParticipation winnerGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(), winner.getClub().getId());
                winnerGains.setPoints(winnerGains.getPoints() + 1);
                winnerGains.setDraws(winnerGains.getDraws() + 1);
                winnerGains.setConcededGoals(winnerGains.getConcededGoals() + losing.getScore());
                List<Goal> winnerGoals = goalCrudOperations.getByClubMatchId(winner.getId());

                //  winnerGains.setScoredGoals(winnerGains.getScoredGoals() + winner.getScore());
                winnerGains.setScoredGoals(winnerGains.getScoredGoals()+  winnerGoals.size() );
               // winnerGains.setScoredGoals(winnerGains.getScoredGoals() + winner.getScore());
                if (losing.getScore() == 0) {
                    winnerGains.setCleanSheetNumber(winnerGains.getCleanSheetNumber() + 1);
                }
                ClubParticipation losingGains = clubParticipationCrudOperations.getBySeasonIdAndClubId(match.getSeason().getId(), losing.getClub().getId());
                losingGains.setPoints(losingGains.getPoints() + 1);
                losingGains.setDraws(losingGains.getDraws() + 1);
                losingGains.setConcededGoals(losingGains.getConcededGoals() + winner.getScore());
                List<Goal> loserGoals = goalCrudOperations.getByClubMatchId(losing.getId());
                losingGains.setScoredGoals(losingGains.getScoredGoals()+ loserGoals.size());
               // losingGains.setScoredGoals(losingGains.getScoredGoals() + losing.getScore());
                if (winner.getScore() == 0) {
                    losingGains.setCleanSheetNumber(losingGains.getCleanSheetNumber() + 1);
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

            List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByMatchId(matchId);

            playerMatches.forEach(playerMatch -> {
                List<Goal> playerGoals = goalCrudOperations.getByPlayerMatchId(playerMatch.getId());
                PlayerStatistics playerStatistics = new PlayerStatistics();
                playerStatistics.setPlayer(playerMatch.getPlayer());
                playerStatistics.setSeason(match.getSeason());
                playerStatistics.setScoredGoals(playerGoals.size());


                playerGoals.forEach(goal -> {
                    goal.setPlayerMatch(playerMatch);
                });
             //   System.out.println(playerMatch.getId() + " goals  " + playerGoals);
               // System.out.println("my club " + playerMatch.getPlayer().getActualClub());

            });
            playerMatches.forEach(playerMatch -> {
                updatedMatch.getClubPlayingHome().getGoals().forEach(goal -> {
                    if (playerMatch.getGoals().contains(goal)) {
                        goal.setPlayerMatch(playerMatch);
                    }
                });
                updatedMatch.getClubPlayingAway().getGoals().forEach(goal -> {
                    if (playerMatch.getGoals().contains(goal)) {
                        goal.setPlayerMatch(playerMatch);
                    }
                });
            });

            updatedMatch.getClubPlayingHome().setGoals(updatedMatch.getClubPlayingHome().getGoals().stream().filter(goal -> goal.getPlayerMatch() != null).toList());
            updatedMatch.getClubPlayingAway().setGoals(updatedMatch.getClubPlayingAway().getGoals().stream().filter(goal -> goal.getPlayerMatch() != null).toList());

            MatchRest updatedMatchRest = matchRestMapper.toRest(updatedMatch);

            List<PlayerMatch> fullTimePlayerMatches = playerMatchCrudOperations.getPlayerMatchesByMatchId(id);
            List<PlayingTime> playingTimeList = new ArrayList<>();
            fullTimePlayerMatches.forEach(playerMatch -> {
                PlayingTime playingTime = playerMatch.getPlayingTime();
                playingTime.setValue(90);
                playerMatch.setPlayingTime(playingTime);
                playingTimeList.add(playingTime);
            });
            playingTimeCrudOperations.saveAll(playingTimeList);

            return ResponseEntity.ok(updatedMatchRest);
        }
        return ResponseEntity.internalServerError().body("internal server error");
    }


    public ResponseEntity<Object> addGoals(String id, List<CreateGoal> goals) {

        Match match = matchCrudOperations.getById(id);
        List<Goal> awayGoals = new ArrayList<>();
        List<Goal> homeGoals = new ArrayList<>();
        if (match == null) {
            return ResponseEntity.badRequest().body("match not found");
        }
        if (match.getActualStatus() != Status.STARTED) {
            return ResponseEntity.badRequest().body("match not started");
        }
        List<Goal> goalsToSave = new ArrayList<>();

        goals.forEach(goal -> {
            List<PlayerMatch> isInTheMatch = playerMatchCrudOperations.getPlayerMatchesByPlayerIdAndMatchId(goal.getScorerId(), id);
            ClubMatch clubMatch = clubMatchCrudOperations.getByClubIdAndMatchId(goal.getClubId(), id);
            Player player = playerCrudOperations.getById(goal.getScorerId());
            List<PlayerClub> playerClubList = playerClubCrudOperations.getPlayerClubsByPlayerId(player.getId());
            player.setClubs(playerClubList);
            if (!isInTheMatch.isEmpty()) {

                PlayerMatch playerMatch = isInTheMatch.get(0);
                playerMatch.setPlayer(player);
                System.out.println("player match " + playerMatch.getPlayer());

                if (playerMatch != null) {
                    Goal goalToSave = new Goal();
                    goalToSave.setMinuteOfGoal(goal.getMinuteOfGoal());
                    System.out.println("club in goal " + goal.getClubId());
                    System.out.println("player's club " + player.getActualClub().getId());

                    if (!goal.getClubId().equals(player.getActualClub().getId())) {
                        goalToSave.setOwnGoal(true);

                    } else {
                        goalToSave.setOwnGoal(false);
                    }
                    goalToSave.setPlayerMatch(playerMatch);
                    goalToSave.setId(UUID.randomUUID().toString());
                    goalToSave.setClubMatch(clubMatch);
                    goalToSave.setSeason(match.getSeason());
                    playerMatch.setGoals(List.of());
                    goalsToSave.add(goalToSave);
                    if (goalToSave.getClubMatch().getId().equals(match.getClubPlayingHome().getId())) {
                        homeGoals.add(goalToSave);
                    }
                    if (goalToSave.getClubMatch().getId().equals(match.getClubPlayingAway().getId())) {
                        awayGoals.add(goalToSave);
                    }
                }
            }
        });
        if (goalsToSave.stream().filter(goal -> goal.getClubMatch() == null).count() > 0) {
            return ResponseEntity.badRequest().body("club not exists");
        }
        List<Goal> savedGoals = goalCrudOperations.saveAll(goalsToSave);
        if (goalsToSave.size() == 0) {
            return ResponseEntity.badRequest().body("goals not saved");
        }
     /*   goalsToSave.forEach(goal -> {
            String clubId = goal.getClubMatch().getClub().getId();
            String seasonId = match.getSeason().getId();
            ClubParticipation clubParticipation = clubParticipationCrudOperations.getBySeasonIdAndClubId(seasonId,clubId);
        //    clubParticipation.setScoredGoals(clubParticipation.getScoredGoals()+1);
            clubParticipation.setClub(goal.getClubMatch().getClub());
            clubParticipationCrudOperations.save(clubParticipation);
        });*/
        // Match updatedMatch = matchCrudOperations.getById(id);
        List<Goal> finalAwayGoals = goalCrudOperations.getByClubMatchId(match.getClubPlayingAway().getId());
        List<Goal> finalHomeGoals = goalCrudOperations.getByClubMatchId(match.getClubPlayingHome().getId());
        List<PlayerMatch> playerMatches = playerMatchCrudOperations.getPlayerMatchesByMatchId(id);
        playerMatches.forEach(playerMatch -> {
            finalAwayGoals.forEach(goal -> {
                if (playerMatch.getGoals().contains(goal)) {
                    goal.setPlayerMatch(playerMatch);
                }
            });
            finalHomeGoals.forEach(goal -> {
                if (playerMatch.getGoals().contains(goal)) {
                    goal.setPlayerMatch(playerMatch);
                }
            });
        });
        match.getClubPlayingAway().setGoals(finalAwayGoals);
        match.getClubPlayingHome().setGoals(finalHomeGoals);
        MatchRest matchRest = matchRestMapper.toRest(match);


        return ResponseEntity.ok(matchRest);

    }
}