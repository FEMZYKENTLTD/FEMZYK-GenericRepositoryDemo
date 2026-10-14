package com.femzyk.genericsdemo.core;

import java.util.function.Function;

/**
 * Repository - a generic data-access interface for any entity type.
 *
 * CS 1103-01, Unit 6 discussion demo. The two type parameters make the
 * contract reusable: T is the entity type and ID is the type of its
 * primary key. One implementation of this interface can therefore store
 * products keyed by Integer, customers keyed by String, or any other
 * combination - without a single cast and with full compile-time safety.
 *
 * @param <T>  the entity type stored in the repository
 * @param <ID> the type of the entity's primary key
 */
public interface Repository<T, ID> {

    /** Creates a factory that pulls the id out of an entity. */
    Function<T, ID> idExtractor();

    /** CREATE / UPDATE: stores an entity under its own id. */
    T save(T entity);

    /** READ: returns the entity with the given id, or null. */
    T findById(ID id);

    /** READ: returns every stored entity. */
    Iterable<T> findAll();

    /** DELETE: removes the entity with the given id. */
    boolean deleteById(ID id);

    /** Returns how many entities are stored. */
    long count();
}
