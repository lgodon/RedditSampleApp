package com.leandrogodon.redditsampleapp.cache;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple list-based cache.
 */
public abstract class AbstractCache<T> {

    protected List<T> cache;

    public AbstractCache() {
        cache = new LinkedList<>();
        readAllFromStorage();
    }

    public void persistAll() {
        PersistenceManager.persistAll(this);
    }

    public void readAllFromStorage() {
        PersistenceManager.readAll(this, getType());
    }

    public void clear() {
        cache.clear();
        PersistenceManager.clear(this);
    }

    public void put(T object) {
        if (object == null) {
            return;
        }

        cache.add(object);
    }

    public boolean remove(T object) {
        return cache.remove(object);
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public void putAll(List<T> objects) {
        if (objects == null) {
            return;
        }
        cache.addAll(objects);
        PersistenceManager.persistAll(this);
    }

    protected abstract Type getType();
}
