package com.upsxace.bio_process_simulator.repository;

import com.upsxace.bio_process_simulator.infrastructure.orm.EntityManager;
import com.upsxace.bio_process_simulator.infrastructure.orm.Repository;
import com.upsxace.bio_process_simulator.model.Experiment;
import com.upsxace.bio_process_simulator.model.Measurement;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
public class MeasurementRepository implements Repository<Measurement> {
    private final EntityManager em;

    @Override
    public Measurement findById(UUID id) {
        return em.findById(Measurement.class, id);
    }

    @Override
    public Set<Measurement> findAll() {
        return em.findAll(Measurement.class);
    }

    @Override
    public Measurement save(Measurement entity) {
        return em.save(Measurement.class, entity);
    }

    @Override
    public Set<Measurement> saveAll(Set<Measurement> entities){
        return em.saveAll(Measurement.class, entities);
    }

    @Override
    public void delete(Measurement entity) {
        em.delete(Measurement.class, entity);
    }

    public Set<Measurement> findByExperimentId(UUID experimentId){
        return em.find(Measurement.class, m -> m.getExperimentId().equals(experimentId)); // TODO: add test
    }
}
