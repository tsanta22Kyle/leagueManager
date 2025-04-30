package com.fifa_app.league_manager.dao.operations;

import java.util.List;

public interface CrudOperations<E> {
    List<E> getAll();
}
