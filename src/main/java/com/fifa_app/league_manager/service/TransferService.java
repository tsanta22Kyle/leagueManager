package com.fifa_app.league_manager.service;


import com.fifa_app.league_manager.dao.operations.PlayerTransferCrudOperations;
import com.fifa_app.league_manager.model.PlayerTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service@RequiredArgsConstructor
public class TransferService {

    private final PlayerTransferCrudOperations playerTransferCrudOperations;

    public ResponseEntity<Object> getAll(){
     List<PlayerTransfer> transfers =  playerTransferCrudOperations.getAll();
     return ResponseEntity.ok(transfers);
    }
}
