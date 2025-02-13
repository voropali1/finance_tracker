package cz.cvut.fel.pm2.UserMicroservice.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class AbstractDao<T> {
    @PersistenceContext
    protected EntityManager manager;
    protected final Class<T> type;

    protected AbstractDao(Class<T> type) {
        this.type = type;
    }

    public T getEntityById(Integer id) {
        Objects.requireNonNull(id);
        return manager.find(type, id);
    }

    public List<T> getAll() {
        try {
            return manager.createQuery("SELECT e FROM " + type.getSimpleName() + " e", type).getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void remove(T entity) {
        Objects.requireNonNull(entity);
        try {
            final T toRemove = manager.merge(entity);
            if (toRemove != null) {
                manager.remove(toRemove);
            }
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public T update(T entity) {
        Objects.requireNonNull(entity);
        try {
            return manager.merge(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(T entity) {
        Objects.requireNonNull(entity);
        try {
            manager.persist(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        if (entities.isEmpty()) {
            return;
        }
        try {
            entities.forEach(this::persist);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public T save(T entity) {
        persist(entity);
        return entity;
    }

    public T find(Integer id) {
        Objects.requireNonNull(id);
        return manager.find(type, id);
    }

    public boolean exists(Integer id) {
        return id != null && manager.find(type, id) != null;
    }
}