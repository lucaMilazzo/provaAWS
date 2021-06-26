package com.deivmercer.kanbanboard.repository;

import java.util.Optional;

public interface KanbanRepository<T, S> {

    Optional<T> findByTitle(S title);
}
