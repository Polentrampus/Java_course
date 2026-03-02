package hotel.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public abstract class BaseRepository<T, ID> {
    private Class<T> entityClass;
    @Autowired
    private SessionFactory sessionFactory;

    public BaseRepository() {
    }
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(getCurrentSession().get(entityClass, (Serializable) id));
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
