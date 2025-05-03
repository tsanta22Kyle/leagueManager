package com.fifa_app.league_manager.endpoint;

import com.fifa_app.league_manager.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("matchMaker/{seasonYear}")
    public Object matchMaker(@PathVariable Year seasonYear) {
        return matchService.createAllMatches(seasonYear);
    }

}
