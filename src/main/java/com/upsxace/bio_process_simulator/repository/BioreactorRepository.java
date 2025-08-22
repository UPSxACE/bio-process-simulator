package com.upsxace.bio_process_simulator.repository;

import com.upsxace.bio_process_simulator.infrastructure.orm.EntityManager;
import com.upsxace.bio_process_simulator.infrastructure.orm.Repository;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BioreactorRepository implements Repository<Bioreactor> {
    private final EntityManager em;

    @Override
    public Bioreactor findById(UUID id) {
        return em.findById(Bioreactor.class, id);
    }

    @Override
    public Set<Bioreactor> findAll() {
        return em.findAll(Bioreactor.class);
    }

    @Override
    public Bioreactor save(Bioreactor entity) {
        return em.save(Bioreactor.class, entity);
    }

    @Override
    public void delete(Bioreactor entity) {
        em.delete(Bioreactor.class, entity);
    }
}
