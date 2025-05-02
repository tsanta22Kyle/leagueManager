package com.fifa_app.league_manager.endpoint;


import com.fifa_app.league_manager.model.Season;
import com.fifa_app.league_manager.model.UpdateSeasonStatus;
import com.fifa_app.league_manager.service.SeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping("")
    public Object getSeasons() {
        return seasonService.getSeasons();
    }

    @PostMapping("")
    public Object saveAll(@RequestBody List<Season> entities) {
        return seasonService.saveAll(entities);
    }

    @PutMapping("/{seasonYear}/status")
    public Object updateStatus(@PathVariable long seasonYear, @RequestBody UpdateSeasonStatus entity) {
        return seasonService.updateStatus(seasonYear, entity);
    }
}
