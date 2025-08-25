package com.upsxace.bio_process_simulator.repository;

import com.upsxace.bio_process_simulator.infrastructure.orm.EntityManager;
import com.upsxace.bio_process_simulator.infrastructure.orm.Repository;
import com.upsxace.bio_process_simulator.model.Experiment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

// TODO: update tests
@Component
@AllArgsConstructor
public class ExperimentRepository implements Repository<Experiment> {
    private final EntityManager em;

    @Override
    public Experiment findById(UUID id) {
        return em.findById(Experiment.class, id);
    }

    @Override
    public Set<Experiment> findAll() {
        return em.findAll(Experiment.class);
    }

    @Override
    public Experiment save(Experiment entity) {
        return em.save(Experiment.class, entity);
    }

    @Override
    public Set<Experiment> saveAll(Set<Experiment> entities) {
        return em.saveAll(Experiment.class, entities);
    }

    @Override
    public void delete(Experiment entity) {
        em.delete(Experiment.class, entity);
    }

    public Set<Experiment> findByActive(boolean active){
        return em.find(Experiment.class, e -> e.isActive() == active); // TODO: add test
    }
}
