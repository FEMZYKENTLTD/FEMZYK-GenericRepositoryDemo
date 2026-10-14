package com.femzyk.genericsdemo.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * InMemoryRepository - one generic implementation for every entity type.
 *
 * CS 1103-01, Unit 6 discussion demo. Written ONCE with the type parameter
 * T, this class stores products, customers, or anything else - the compiler
 * produces a separate type-safe view for each usage. Backed by a HashMap,
 * so lookups by id are O(1).
 *
 * @param <T>  the entity type stored in the repository
 * @param <ID> the type of the entity's primary key
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {

    /** Extracts the id from an entity (provided by the caller). */
    private final Function<T, ID> idExtractor;

    /** The underlying type-safe storage. */
    private final Map<ID, T> storage = new HashMap<>();

    /**
     * Creates a repository for one entity type.
     *
     * @param idExtractor a function returning the id of any entity
     */
    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public Function<T, ID> idExtractor() {
        return idExtractor;
    }

    @Override
    public T save(T entity) {
        storage.put(idExtractor.apply(entity), entity);
        return entity;
    }

    @Override
    public T findById(ID id) {
        return storage.get(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(ID id) {
        return storage.remove(id) != null;
    }

    @Override
    public long count() {
        return storage.size();
    }
}
