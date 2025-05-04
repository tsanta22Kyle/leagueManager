package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.ClubMatchRest;
import com.fifa_app.league_manager.endpoint.rest.ScorerRest;
import com.fifa_app.league_manager.model.ClubMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component@RequiredArgsConstructor
public class ClubMatchRestMapper {

    private final ScorerRestMapper scorerRestMapper;

    public ClubMatchRest toRest(ClubMatch clubMatch) {
        ClubMatchRest clubMatchRest = new ClubMatchRest();
        clubMatchRest.setId(clubMatch.getId());
        clubMatchRest.setScore(clubMatch.getScore());
        clubMatchRest.setName(clubMatch.getClub().getName());
        clubMatchRest.setAcronym(clubMatch.getClub().getAcronym());
        List<ScorerRest> scorerRests = new ArrayList<>();
        clubMatch.getGoals().forEach(goal -> {
            ScorerRest scorerRest = scorerRestMapper.toRest(goal);
            scorerRests.add(scorerRest);
        });
        clubMatchRest.setScorers(scorerRests);
        return clubMatchRest;
    }

}
