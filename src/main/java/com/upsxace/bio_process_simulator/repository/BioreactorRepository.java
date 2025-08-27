package com.upsxace.bio_process_simulator.repository;

import com.upsxace.bio_process_simulator.infrastructure.orm.EntityManager;
import com.upsxace.bio_process_simulator.infrastructure.orm.Repository;
import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public Set<Bioreactor> saveAll(Set<Bioreactor> entities){
        return em.saveAll(Bioreactor.class, entities);
    }

    @Override
    public void delete(Bioreactor entity) {
        em.delete(Bioreactor.class, entity);
    }

    public Set<Bioreactor> findByIdIn(List<UUID> ids){
        return em.find(Bioreactor.class, b -> ids.contains(b.getId())); // TODO: add test
    }

    public Set<Bioreactor> findByStatus(BioreactorStatus status){
        return em.find(Bioreactor.class, b -> b.getStatus().equals(status)); // TODO: add test
    }

    public Set<Bioreactor> findByStatusAndIdIn(BioreactorStatus status, List<UUID> ids){
        return em.find(Bioreactor.class, b -> b.getStatus().equals(status) && ids.contains(b.getId())); // TODO: add test
    }

    public int countByStatusInAndIdIn(List<BioreactorStatus> statuses, List<UUID> ids){
        return em.find(Bioreactor.class, b -> statuses.contains(b.getStatus()) && ids.contains(b.getId())).size(); // TODO: add test
    }
}
