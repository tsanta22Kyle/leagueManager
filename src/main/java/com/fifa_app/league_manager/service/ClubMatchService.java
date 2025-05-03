package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.ClubMatchCrudOperations;
import com.fifa_app.league_manager.dao.operations.MatchCrudOperations;
import com.fifa_app.league_manager.endpoint.rest.ClubRest;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.ClubMatch;
import com.fifa_app.league_manager.model.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubMatchService {
    private final ClubMatchCrudOperations clubMatchCrudOperations;
    private final MatchCrudOperations matchCrudOperations;


    public List<ClubMatch> getAll() {
        List<ClubMatch> clubMatches = clubMatchCrudOperations.getAll();

        this.setMatchToClubMatch(clubMatches);

        return clubMatches;
    }


    public List<ClubMatch> getManyByClubId(String clubId) {
        List<ClubMatch> clubMatches = clubMatchCrudOperations.getManyByClubId(clubId);

        this.setMatchToClubMatch(clubMatches);

        return clubMatches;
    }


    public ClubMatch getById(String id) {
        ClubMatch clubMatch = clubMatchCrudOperations.getById(id);

        this.setMatchToClubMatch(List.of(clubMatch));

        return clubMatch;
    }


    private void setMatchToClubMatch(List<ClubMatch> clubMatches) {
        List<Match> matches = matchCrudOperations.getAll();

        System.out.println(matches.size() > clubMatches.size());

        clubMatches.forEach(clubMatch -> {
            matches.forEach(match -> {

                if (clubMatch.getId().equals(match.getClubPlayingHome().getId())
                        || clubMatch.getId().equals(match.getClubPlayingAway().getId())
                ) {
                    clubMatch.setMatch(match);
                    System.out.println(clubMatch.getId() + " hit " + clubMatch.getMatch().getId());
                } else {
                    System.out.println(clubMatch.getId());
                }
            });
        });
    }
}
