package com.fifa_app.league_manager.service;

import com.fifa_app.league_manager.dao.operations.SeasonCrudOperations;
import com.fifa_app.league_manager.model.Season;
import com.fifa_app.league_manager.model.Status;
import com.fifa_app.league_manager.model.UpdateSeasonStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        List<Season> seasons = seasonCrudOperations.getAll();
        if (
                seasons.stream().anyMatch(season -> season.getStatus().equals(Status.STARTED)) &&
                entity.getStatus().equals(Status.STARTED)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot START this season because another season is still ACTIVE.");
        }

        Season season = seasonCrudOperations.updateStatus(year, entity);
        return ResponseEntity.ok().body(season);
    }

}
