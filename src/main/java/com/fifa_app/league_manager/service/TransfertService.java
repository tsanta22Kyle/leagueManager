package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.PlayerTransferCrudOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransfertService {

    private final PlayerTransferCrudOperations playerTransferCrudOperations;

    public TransfertService(PlayerTransferCrudOperations playerTransferCrudOperations) {
        this.playerTransferCrudOperations = playerTransferCrudOperations;
    }

    public ResponseEntity<Object> getAll(){
         playerTransferCrudOperations.getAll();
    }
}
