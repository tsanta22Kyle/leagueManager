package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.ClubMatchRest;
import com.fifa_app.league_manager.model.ClubMatch;
import org.springframework.stereotype.Component;

@Component
public class ClubMatchRestMapper {

    public ClubMatchRest toRest(ClubMatch clubMatch) {
        ClubMatchRest clubMatchRest = new ClubMatchRest();
        clubMatchRest.setId(clubMatch.getId());
        clubMatchRest.setScore(clubMatch.getScore());
        clubMatchRest.setName(clubMatch.getClub().getName());
        clubMatchRest.setAcronym(clubMatch.getClub().getAcronym());
        return clubMatchRest;
    }

}
