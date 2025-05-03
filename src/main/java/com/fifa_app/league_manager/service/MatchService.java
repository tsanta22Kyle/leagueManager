package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.ClubMatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.ClubParticipationCrudOperations;
import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Year;
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

        // 1. Créer les matchs (sans clubPlayingHome ni clubPlayingAway)
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

        // 2. Créer les ClubMatch associés aux Match existants
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

        // 3. Update des Matchs pour lier les ClubMatchs créés
        for (Match match : matchesToCreate) {
            match.setClubPlayingHome(homeClubMatchMap.get(match.getId()));
            match.setClubPlayingAway(awayClubMatchMap.get(match.getId()));
        }

        matchCrudOperations.saveAll(matchesToCreate); // update

        return ResponseEntity.ok(matchesToCreate);
    }



    public void assignConsecutiveDates(List<Match> matches, Instant startDate) {
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).setMatchDatetime(startDate.plus(i, ChronoUnit.DAYS));
        }
    }

}
