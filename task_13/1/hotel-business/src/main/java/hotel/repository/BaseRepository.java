package hotel.repository;

import hotel.service.SessionContext;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {
    private Class<T> entityClass;

    public BaseRepository() {
    }
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    protected Session getCurrentSession() {
        return SessionContext.getCurrentSession();
    }

    public Optional<T> findById(ID id) {
        return Optional.of(getCurrentSession().get(entityClass, (Serializable) id));
    }

    public ID save(T entity) {
        return (ID) getCurrentSession().save(entity);
    }

    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    public void update(T entity) {
        getCurrentSession().update(entity);
    }

    public List<T> findAll() {
        return getCurrentSession()
                .createQuery("from " + entityClass.getName(), entityClass)
                .list();
    }
}
