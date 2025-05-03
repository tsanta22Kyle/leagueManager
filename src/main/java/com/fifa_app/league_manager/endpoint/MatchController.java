package com.fifa_app.league_manager.endpoint;

import com.fifa_app.league_manager.model.Status;
import com.fifa_app.league_manager.service.MatchService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("matchMaker/{seasonYear}")
    public Object matchMaker(@PathVariable Year seasonYear) {
        return matchService.createAllMatches(seasonYear);
    }

    @GetMapping("matches/{seasonYear}")
    public Object matches(@PathVariable Year seasonYear, @RequestParam(defaultValue = "NOT_STARTED") Status matchStatus , @RequestParam(defaultValue = "",required = false) String clubPlayingName , @RequestParam(required = false,defaultValue =  "2000-01-01"  ) LocalDate matchAfter , @RequestParam(required = false,defaultValue = "2050-12-25") LocalDate matchBeforeOrEquals) {
        return matchService.getAllSeasonsMatches(seasonYear,matchStatus,clubPlayingName,matchAfter,matchBeforeOrEquals);
    }

}
