package com.upsxace.bio_process_simulator.infrastructure.orm;

import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntityManagerTest {
    static Map<Class<?>, String> entitiesThatShouldLoad = Map.of(
            Bioreactor.class, "bioreactor"
    );

    @Autowired
    EntityManager em;

    private Bioreactor sampleBioreactor(){
        return new Bioreactor("CHO-K1");
    }

    private <T> void deleteAllOfClass(Class<T> clazz){
        var all = em.findAll(clazz);
        all.forEach(e -> em.delete(clazz, e));
    }

    @BeforeEach
    public void setUp() {
        // delete all entries in database before each test
        entitiesThatShouldLoad.forEach((clazz, name) -> deleteAllOfClass(clazz));
    }

    static Stream<Arguments> entityArgumentsProvider(){
        return entitiesThatShouldLoad
                .entrySet()
                .stream()
                .map((e) -> Arguments.of(e.getKey(), e.getValue()));
    }

    @ParameterizedTest
    @MethodSource("entityArgumentsProvider")
    void shouldLoadAllEntitiesAnnotatedWithEntityAnnotation(Class<?> clazz, String entityName) {
        assertTrue(em.getDatabase().containsKey(entityName));
        assertNotNull(em.getDatabase().get(entityName));
        assertTrue(em.getClassNameToEntityName().containsKey(clazz.getName()));
        assertEquals(em.getClassNameToEntityName().get(clazz.getName()), entityName);
        assertTrue(em.getClassNameToIdField().containsKey(clazz.getName()));
    }

    @Test
    void save_givenLoadedEntity_shouldSaveAndGenerateId() {
        var bioreactor = sampleBioreactor();

        var id = bioreactor.getId();
        assertNull(id);

        em.save(Bioreactor.class, bioreactor);
        id = bioreactor.getId();
        assertNotNull(id);

        var savedEntity = em.findById(Bioreactor.class, id);
        assertEquals(bioreactor, savedEntity);
    }

    @Test
    void findById_givenExistingId_shouldFindEntity() {
        var bioreactor = sampleBioreactor();

        em.save(Bioreactor.class, bioreactor);
        var id = bioreactor.getId();
        var savedEntity = em.findById(Bioreactor.class, id);

        assertEquals(bioreactor, savedEntity);
    }

    @Test
    void findById_givenNonExistingId_shouldReturnNull() {
        assertNull(em.findById(Bioreactor.class, UUID.randomUUID()));
    }

    @Test
    void findAll_shouldFindAllSavedEntities() {
        var entitiesCount = 10;

        var savedEntities = new ArrayList<Bioreactor>();
        for(var i = 0; i < entitiesCount; i++){
            var savedEntity = em.save(Bioreactor.class, sampleBioreactor());
            savedEntities.add(savedEntity);
        }

        var allEntitiesSaved = em.findAll(Bioreactor.class);

        assertEquals(entitiesCount, allEntitiesSaved.size());
        assertTrue(allEntitiesSaved.containsAll(savedEntities));
    }

    @Test
    void delete_shouldDeleteEntityFromDatabase() {
        var entitiesCount = 10;

        var savedEntities = new ArrayList<Bioreactor>();
        for(var i = 0; i < entitiesCount; i++){
            var savedEntity = em.save(Bioreactor.class, sampleBioreactor());
            savedEntities.add(savedEntity);
        }

        // delete first
        var first = savedEntities.getFirst();
        assertNotNull( em.findById(Bioreactor.class, first.getId()));
        em.delete(Bioreactor.class, first);
        assertNull(em.findById(Bioreactor.class,first.getId()));

        var allEntities = em.findAll(Bioreactor.class);

        for(var i = 0; i < entitiesCount; i++){
            if(i == 0) {
                // first should not be in database
                assertFalse(allEntities.contains(savedEntities.get(i)));
                continue;
            }

            // others should be in database
            assertTrue(allEntities.contains(savedEntities.get(i)));
        }
    }

    @Test
    void delete_givenNonExistingEntity_shouldNotThrowError() {
        var entity = sampleBioreactor();
        em.save(Bioreactor.class, entity);
        assertNotNull(em.findById(Bioreactor.class, entity.getId()));
        em.delete(Bioreactor.class, entity);
        assertNull(em.findById(Bioreactor.class, entity.getId()));
        assertDoesNotThrow(() -> em.delete(Bioreactor.class, entity)); // delete non existing entity
    }
}