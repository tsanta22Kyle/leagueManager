package com.fifa_app.league_manager.endpoint;

import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clubs")
public class ClubController {
    private final ClubService clubService;

    @GetMapping("")
    public Object getClubs() {
        return clubService.getClubs();
    }

    @PostMapping("")
    public Object saveAll(@RequestBody() List<Club> entities){
        return clubService.saveAll(entities);
    }
}
