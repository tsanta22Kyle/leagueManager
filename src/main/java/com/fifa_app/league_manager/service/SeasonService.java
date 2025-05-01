package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.Season;
import com.fifa_app.league_manager.model.UpdateSeasonStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonCrudOperations seasonCrudOperations;

    public ResponseEntity<Object> getSeasons() {
        List<Season> seasons = seasonCrudOperations.getAll();
        return ResponseEntity.ok().body(seasons);
    }

    public ResponseEntity<Object> saveAll(List<Season> entities) {
        List<Season> seasons = seasonCrudOperations.saveAll(entities);
        return ResponseEntity.ok().body(seasons);
    }

    public ResponseEntity<Object> updateStatus(long year, UpdateSeasonStatus entity) {
        Season season = seasonCrudOperations.updateStatus(year, entity);
        return ResponseEntity.ok().body(season);
    }

}
