package com.deivmercer.kanbanboard.repository;

import com.deivmercer.kanbanboard.model.Column;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ColumnRepository extends CrudRepository<Column, Integer> {

    Optional<Column> findByTitle(String title);
}
