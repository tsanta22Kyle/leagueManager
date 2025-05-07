package com.fifa_app.league_manager.dao.operations;


import com.fifa_app.league_manager.dao.DataSource;
import com.fifa_app.league_manager.model.PlayerTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerTransferCrudOperations implements CrudOperations<PlayerTransfer> {
    private final DataSource dataSource;


    @Override
    public List<PlayerTransfer> getAll() {
        return List.of();
    }
}
