package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.ScorerRest;
import com.fifa_app.league_manager.model.Goal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component@RequiredArgsConstructor
public class ScorerRestMapper {

    private final PlayerRestMapper playerRestMapper;

    public ScorerRest toRest(Goal goal){
        ScorerRest scorerRest = new ScorerRest();
        scorerRest.setPlayer(playerRestMapper.toRest(goal.getPlayerMatch().getPlayer()));
        scorerRest.setOwnGoal(goal.isOwnGoal());
        scorerRest.setMinuteOfGoal(goal.getMinuteOfGoal());
        return scorerRest;
    }
}
