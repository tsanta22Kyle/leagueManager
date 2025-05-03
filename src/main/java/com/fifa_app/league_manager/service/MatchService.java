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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        if (existingMatches.size() > 0) {
            return ResponseEntity.badRequest().body("matches already exist");
        }

        List<ClubParticipation> participatingClubs = clubParticipationCrudOperations.getBySeasonId(season.getId());

        System.out.println("participatingClubs: " + participatingClubs);
        List<Match> matchesToCreate = new ArrayList<>();

        for (int i = 0; i < participatingClubs.size(); i++) {
            for (int j = 0; j < participatingClubs.size(); j++) {
                if (i == j) continue; // Un club ne joue pas contre lui-même
                String matchId = UUID.randomUUID().toString();
                ClubMatch homeClubMatchToSave = new ClubMatch();
                homeClubMatchToSave.setClub(participatingClubs.get(i).getClub());
                homeClubMatchToSave.setMatch(new Match(matchId));
                homeClubMatchToSave.setId(UUID.randomUUID().toString());
                ClubMatch savedClubHomeMatch = clubMatchCrudOperations.save(homeClubMatchToSave);

                ClubMatch awayClubMatchToSave = new ClubMatch();
                awayClubMatchToSave.setClub(participatingClubs.get(j).getClub());
                awayClubMatchToSave.setMatch(new Match(matchId));
                awayClubMatchToSave.setId(UUID.randomUUID().toString());
                ClubMatch savedClubAwayMatch = clubMatchCrudOperations.save(awayClubMatchToSave);


                Match match = new Match();
                match.setId(matchId);

                match.setSeason(season);
                match.setClubPlayingHome(savedClubHomeMatch);
                match.setClubPlayingAway(savedClubAwayMatch);
                match.setActualStatus(Status.NOT_STARTED);

                matchesToCreate.add(match);
            }
        }
        assignConsecutiveDates(matchesToCreate, Instant.now());
        List<Match> createdMatches = matchCrudOperations.saveAll(matchesToCreate);

        return ResponseEntity.ok(createdMatches);
    }


    public void assignConsecutiveDates(List<Match> matches, Instant startDate) {
        for (int i = 0; i < matches.size(); i++) {
            matches.get(i).setMatchDatetime(startDate.plus(i, ChronoUnit.DAYS));
        }
    }

}
