package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.MatchRest;
import com.fifa_app.league_manager.model.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component@RequiredArgsConstructor
public class MatchRestMapper {
    private final ClubMatchRestMapper clubMatchRestMapper;

    public MatchRest toRest(Match match) {
        MatchRest matchRest = new MatchRest();
        matchRest.setId(match.getId());
        matchRest.setMatchDateTime(match.getMatchDatetime());
        matchRest.setStadium(match.getStadium());
        matchRest.setActualStatus(match.getActualStatus());
        matchRest.setClubPlayingHome(clubMatchRestMapper.toRest(match.getClubPlayingHome()));
        matchRest.setClubPlayingAway(clubMatchRestMapper.toRest(match.getClubPlayingAway()));
        return matchRest;
    }
}
