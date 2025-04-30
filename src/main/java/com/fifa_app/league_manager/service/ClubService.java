package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.ClubOperations;
import com.fifa_app.league_manager.model.Club;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {
    private final ClubOperations clubOperations;

    public ResponseEntity<Object> getClubs() {
        List<Club> clubs = clubOperations.getAll();

        return ResponseEntity.ok().body(clubs);
    }

    public ResponseEntity<Object> saveAll(List<Club> entities) {
        List<Club> clubs = clubOperations.saveAll(entities);

        return ResponseEntity.ok().body(clubs);
    }
}
