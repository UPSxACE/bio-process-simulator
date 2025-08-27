package com.upsxace.bio_process_simulator.repository;

import com.upsxace.bio_process_simulator.infrastructure.orm.EntityManager;
import com.upsxace.bio_process_simulator.infrastructure.orm.Repository;
import com.upsxace.bio_process_simulator.model.Report;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ReportRepository implements Repository<Report> {
    private final EntityManager em;

    @Override
    public Report findById(UUID id) {
        return em.findById(Report.class, id);
    }

    @Override
    public Set<Report> findAll() {
        return em.findAll(Report.class);
    }

    @Override
    public Report save(Report entity) {
        return em.save(Report.class, entity);
    }

    @Override
    public Set<Report> saveAll(Set<Report> entities){
        return em.saveAll(Report.class, entities);
    }

    @Override
    public void delete(Report entity) {
        em.delete(Report.class, entity);
    }
}
