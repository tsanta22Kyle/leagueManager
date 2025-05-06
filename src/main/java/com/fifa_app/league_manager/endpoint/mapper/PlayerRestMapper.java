package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.PlayerRest;
import com.fifa_app.league_manager.model.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerRestMapper {

    public PlayerRest toRest(Player player) {
        PlayerRest playerRest = new PlayerRest();
        playerRest.setId(player.getId());
        playerRest.setName(player.getName());
        playerRest.setNumber(player.getNumber());
        return playerRest;
    }
}
