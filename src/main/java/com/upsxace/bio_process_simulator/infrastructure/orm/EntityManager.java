package com.upsxace.bio_process_simulator.infrastructure.orm;

import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Entity;
import com.upsxace.bio_process_simulator.infrastructure.orm.annotation.Id;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class EntityManager {
    private final Map<String, Set<Object>> database = new HashMap<>();
    private final Map<String, String> classNameToEntityName = new HashMap<>();
    private final Map<String, Field> classNameToIdField = new HashMap<>();

    public Map<String, Set<Object>> getDatabase(){
        return Map.copyOf(database); // readonly
    }

    public Map<String, String> getClassNameToEntityName(){
        return Map.copyOf(classNameToEntityName); // readonly
    }

    public Map<String, Field> getClassNameToIdField(){
        return Map.copyOf(classNameToIdField); // readonly
    }

    private Field getIdField(Class<?> clazz){
        for (var field : clazz.getDeclaredFields()){
            if(field.isAnnotationPresent(Id.class)){
                if(field.getType()  != UUID.class)
                    throw new IllegalStateException("@Id annotation can only be used in fields of type UUID");
                field.setAccessible(true);
                return field;
            }

        }
        throw new IllegalStateException("@Id annotation missing in entity class " + clazz.getName());
    }

    private <T> UUID getIdFromEntity(Class<T> clazz, T entity) {
        var idField = classNameToIdField.get(clazz.getName());
        try {
            return (UUID) idField.get(entity);
        } catch(Exception ex) {
            return null;
        }
    }

    private <T> void generateIdForEntity(Class<T> clazz, T entity) throws IllegalAccessException {
        var idField = classNameToIdField.get(clazz.getName());
        idField.set(entity, UUID.randomUUID());
    }

    @PostConstruct
    public void loadEntities() throws ClassNotFoundException {
        var scanner = new ClassPathScanningCandidateComponentProvider(false); // scanner with no defaults
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class)); // filter by annotations of type Entity
        var candidates = scanner.findCandidateComponents("com.upsxace"); // search in whole com.upsxace package
        for (var candidate : candidates){
            String className = candidate.getBeanClassName();

            Class<?> clazz = Class.forName(className);  // instantiate Class object based on the package name
            var entityName = clazz.getAnnotation(Entity.class).name();

            classNameToEntityName.put(className, entityName);
            classNameToIdField.put(className, getIdField(clazz));
            database.put(entityName, new HashSet<>());
        }
    }

    // FIXME: make copies on query and insert methods instead of returning references & add tests about copy vs references

    @SuppressWarnings("unchecked")
    public <T> Set<T> find(Class<T> type, Predicate<T> filter){
        // TODO: add test
        return database.get(classNameToEntityName.get(type.getName()))
                .stream()
                .filter((Predicate<Object>) filter)
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    // FIXME: refactor class to add id hashmap
    public <T> T findById(Class<T> type, UUID id){
        return database.get(classNameToEntityName.get(type.getName()))
                .stream()
                .filter(o -> Objects.equals(getIdFromEntity(type, type.cast(o)), id))
                .findFirst()
                .map(type::cast)
                .orElse(null);
    }

    public <T> Set<T> findAll(Class<T> type){
        return database.get(classNameToEntityName.get(type.getName()))
                .stream()
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    public <T> T save(Class<T> type, T entity) {
        database.computeIfPresent(classNameToEntityName.get(type.getName()), (name, list) -> {
            var idValue = getIdFromEntity(type, entity);
            if(idValue == null) {
                try {
                    generateIdForEntity(type, entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            list.add(entity);
            return list;
        });
        return entity;
    }

    // TODO: add test
    public <T> Set<T> saveAll(Class<T> type, Set<T> entities) {
        for(var entity : entities){
            save(type, entity);
        }
        return entities;
    }

    public <T> void delete(Class<T> type, T entity){
        final var entityId = getIdFromEntity(type, entity);
        database.computeIfPresent(classNameToEntityName.get(type.getName()), (name, set) ->
                set.stream()
                        .filter(e -> !Objects.equals(getIdFromEntity(type, type.cast(e)), entityId))
                        .collect(Collectors.toSet())
        );
    }
}
