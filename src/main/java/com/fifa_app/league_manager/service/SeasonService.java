package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.SeasonOperations;
import com.fifa_app.league_manager.model.Club;
import com.fifa_app.league_manager.model.Season;
import com.fifa_app.league_manager.model.SeasonStatus;
import com.fifa_app.league_manager.model.UpdateSeasonStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonOperations seasonOperations;

    public ResponseEntity<Object> getSeasons() {
        List<Season> seasons = seasonOperations.getAll();
        return ResponseEntity.ok().body(seasons);
    }

    public ResponseEntity<Object> saveAll(List<Season> entities) {
        List<Season> seasons = seasonOperations.saveAll(entities);
        return ResponseEntity.ok().body(seasons);
    }

    public ResponseEntity<Object> updateStatus(long year, UpdateSeasonStatus entity) {
        Season season = seasonOperations.updateStatus(year, entity);
        return ResponseEntity.ok().body(season);
    }

}
