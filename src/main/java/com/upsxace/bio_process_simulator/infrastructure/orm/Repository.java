package com.upsxace.bio_process_simulator.infrastructure.orm;

import java.util.Set;
import java.util.UUID;

// TODO: replace T by Optionals
public interface Repository <T> {
    T findById(UUID id);
    Set<T> findAll();
    T save(T entity);
    Set<T> saveAll(Set<T> entities);
    void delete(T entity);
}
