package com.upsxace.bio_process_simulator.infrastructure.orm;

import java.util.Set;
import java.util.UUID;

public interface Repository <T> {
    T findById(UUID id);
    Set<T> findAll();
    T save(T entity);
    void delete(T entity);
}
