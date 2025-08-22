package com.upsxace.bio_process_simulator.infrastructure.orm;

import com.upsxace.bio_process_simulator.model.Bioreactor;
import com.upsxace.bio_process_simulator.model.enums.BioreactorStatus;
import com.upsxace.bio_process_simulator.repository.BioreactorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RepositoryTest {
    @FunctionalInterface
    interface Factory<T> {
        T create();
    }

    @Autowired
    EntityManager em;

    @Autowired
    ApplicationContext context;

    static Set<Class<?>> entityClasses = Set.of(Bioreactor.class);

    static <T> Arguments repoArgs(Class<? extends Repository<T>> repoClass, Supplier<T> sampleFactory, Function<T, UUID> getEntityId) {
        return Arguments.of(repoClass, sampleFactory, getEntityId);
    }

    static Stream<Arguments> repositoriesArgumentsProvider() {
        return Stream.of(
                repoArgs(
                        BioreactorRepository.class,
                        () -> Bioreactor.builder()
                                .status(BioreactorStatus.ENDED)
                                .cellType("CHO-K1")
                                .lastSampleTime(LocalDateTime.MIN)
                                .pH(0)
                                .temperature(0)
                                .dissolvedOxygen(0)
                                .glucose(0)
                                .lactate(0)
                                .productTiter(0)
                                .build(),
                        (Bioreactor::getId)
                ));
    }

    private <T> void deleteAllOfClass(Class<T> clazz) {
        var all = em.findAll(clazz);
        all.forEach(e -> em.delete(clazz, e));
    }

    @BeforeEach
    public void setUp() {
        // delete all entries in database before each test
        entityClasses.forEach(this::deleteAllOfClass);
    }

    private <T, Y extends Repository<T>> Y classToInstance(Class<Y> clazz){
        return context.getBean(clazz);
    }

    @ParameterizedTest
    @MethodSource("repositoriesArgumentsProvider")
    <T> void save_givenLoadedEntity_shouldSaveAndGenerateId(
            Class<? extends Repository<T>> clazz,
            Supplier<T> sampleFactory,
            Function<T, UUID> getEntityId
    ) {
        var sampleEntity = sampleFactory.get();
        var id = getEntityId.apply(sampleEntity);
        assertNull(id);

        var repository = classToInstance(clazz);
        repository.save(sampleEntity);

        id = getEntityId.apply(sampleEntity);
        assertNotNull(id);

        var savedEntity = repository.findById(id);
        assertEquals(sampleEntity, savedEntity);
    }

    @ParameterizedTest
    @MethodSource("repositoriesArgumentsProvider")
    <T> void findById_givenExistingId_shouldFindEntity(
            Class<? extends Repository<T>> clazz,
            Supplier<T> sampleFactory,
            Function<T, UUID> getEntityId
    ) {
        var sampleEntity = sampleFactory.get();
        var repository = classToInstance(clazz);

        repository.save(sampleEntity);

        var id = getEntityId.apply(sampleEntity);
        var savedEntity = repository.findById(id);

        assertEquals(sampleEntity, savedEntity);
    }

    @ParameterizedTest
    @MethodSource("repositoriesArgumentsProvider")
    <T> void findById_givenNonExistingId_shouldReturnNull(
            Class<? extends Repository<T>> clazz
    ) {
        var repository = classToInstance(clazz);
        assertNull(repository.findById(UUID.randomUUID()));
    }

    @ParameterizedTest
    @MethodSource("repositoriesArgumentsProvider")
    <T> void findAll_shouldFindAllSavedEntities(
            Class<? extends Repository<T>> clazz,
            Supplier<T> sampleFactory,
            Function<T, UUID> getEntityId
    ) {
        var entitiesCount = 10;
        var repository = classToInstance(clazz);

        var savedEntities = new ArrayList<T>();
        for(var i = 0; i < entitiesCount; i++){
            var savedEntity = repository.save(sampleFactory.get());
            savedEntities.add(savedEntity);
        }

        var allEntitiesSaved = repository.findAll();

        assertEquals(entitiesCount, allEntitiesSaved.size());
        assertTrue(allEntitiesSaved.containsAll(savedEntities));
    }

    @ParameterizedTest
    @MethodSource("repositoriesArgumentsProvider")
    <T> void delete_shouldDeleteEntityFromDatabase(
            Class<? extends Repository<T>> clazz,
            Supplier<T> sampleFactory,
            Function<T, UUID> getEntityId
    ) {
        var entitiesCount = 10;
        var repository = classToInstance(clazz);

        var savedEntities = new ArrayList<T>();
        for(var i = 0; i < entitiesCount; i++){
            var savedEntity = repository.save(sampleFactory.get());
            savedEntities.add(savedEntity);
        }

        // delete first
        var first = savedEntities.getFirst();
        assertNotNull(repository.findById(getEntityId.apply(first)));
        repository.delete(first);
        assertNull(repository.findById(getEntityId.apply(first)));

        var allEntities = repository.findAll();

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

    @ParameterizedTest
    @MethodSource("repositoriesArgumentsProvider")
    <T> void delete_givenNonExistingEntity_shouldNotThrowError(
            Class<? extends Repository<T>> clazz,
            Supplier<T> sampleFactory,
            Function<T, UUID> getEntityId
    ) {
        var entity = sampleFactory.get();
        var repository = classToInstance(clazz);

        repository.save(entity);
        assertNotNull(repository.findById(getEntityId.apply(entity)));
        repository.delete(entity);
        assertNull(em.findById(Bioreactor.class, getEntityId.apply(entity)));
        assertDoesNotThrow(() -> repository.delete(entity)); // delete non existing entity
    }
}