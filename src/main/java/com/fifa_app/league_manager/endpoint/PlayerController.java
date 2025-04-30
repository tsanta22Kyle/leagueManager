package com.fifa_app.league_manager.endpoint;

import com.fifa_app.league_manager.model.Player;
import com.fifa_app.league_manager.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    @GetMapping("")
    public Object getAllPlayers() {
       return playerService.getAllPlayers();
    }

}
