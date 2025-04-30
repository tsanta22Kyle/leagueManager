package com.fifa_app.league_manager.endpoint;

import com.fifa_app.league_manager.endpoint.rest.CreateOrUpdatePlayer;
import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    @GetMapping("")
    public Object getAllPlayers(
            @RequestParam( required = false ,defaultValue = "") String name ,@RequestParam(required = false,defaultValue = "0") int ageMinimum,@RequestParam(required = false , defaultValue = "1000") int ageMaximum ,@RequestParam(required = false,defaultValue = "") String clubName
    ) {
       return playerService.getAllPlayers(name,ageMinimum,ageMaximum,clubName);
    }

    @PutMapping("")
    public Object savePlayers(@RequestBody List<CreateOrUpdatePlayer> players) {
        return playerService.saveAll(players);
    }

}
