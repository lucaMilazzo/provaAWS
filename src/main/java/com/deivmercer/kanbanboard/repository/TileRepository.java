package com.deivmercer.kanbanboard.repository;

import com.deivmercer.kanbanboard.model.Tile;
import org.springframework.data.repository.CrudRepository;

public interface TileRepository extends CrudRepository<Tile, Integer>, KanbanRepository<Tile, String> {
}
