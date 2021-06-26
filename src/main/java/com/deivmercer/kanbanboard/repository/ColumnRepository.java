package com.deivmercer.kanbanboard.repository;

import com.deivmercer.kanbanboard.model.Column;
import org.springframework.data.repository.CrudRepository;

public interface ColumnRepository extends CrudRepository<Column, Integer>, KanbanRepository<Column, String> {
}
