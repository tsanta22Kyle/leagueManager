package com.fifa_app.league_manager.endpoint.mapper;

import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.Player;
import org.springframework.stereotype.Component;

@Component
public class CreateOrUpdatePlayerMapper {
    public Player toModel(CreateOrUpdatePlayer createOrUpdatePlayerMapper) {
        Player player = new Player();
        player.setPosition(createOrUpdatePlayerMapper.getPosition());
        player.setName(createOrUpdatePlayerMapper.getName());
        player.setAge(createOrUpdatePlayerMapper.getAge());
        player.setId(createOrUpdatePlayerMapper.getId());
        player.setCountry(createOrUpdatePlayerMapper.getNationality());
        player.setPreferredNumber(createOrUpdatePlayerMapper.getNumber());
        return player;
    }
    public CreateOrUpdatePlayer toRest(Player player) {
        CreateOrUpdatePlayer createOrUpdatePlayer= new CreateOrUpdatePlayer();
        createOrUpdatePlayer.setPosition(player.getPosition());
        createOrUpdatePlayer.setName(player.getName());
        createOrUpdatePlayer.setAge(player.getAge());
        createOrUpdatePlayer.setId(player.getId());
        createOrUpdatePlayer.setNationality(player.getCountry());
        createOrUpdatePlayer.setNumber(player.getNumber());
        return createOrUpdatePlayer;
    }
}
