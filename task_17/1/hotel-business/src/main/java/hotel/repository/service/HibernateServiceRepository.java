package hotel.repository.service;

import hotel.model.service.Services;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HibernateServiceRepository extends BaseRepository<Services, Integer> implements ServicesRepository {

    public HibernateServiceRepository() {
        setEntityClass(Services.class);
    }

    @Override
    public Optional<Services> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(Services.class, id)),
                "id", id
        );
    }

    @Override
    public List<Services> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from Services", Services.class).list()
        );
    }

    @Override
    public Integer save(Services entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "serviceName", entity.getName(),
                "price", entity.getPrice()
        );
    }

    @Override
    public void update(Services entity) {
        execute("update",
                session -> session.update(entity),
                "serviceId", entity.getId(),
                "serviceName", entity.getName()
        );
    }

    @Override
    public void delete(Services entity) {
        execute("delete",
                session -> {
                    // Проверяем, используется ли услуга в бронированиях
                    Long usageCount = (Long) session.createQuery(
                                    "select count(b) from Bookings b join b.services s where s.id = :serviceId")
                            .setParameter("serviceId", entity.getId())
                            .uniqueResult();

                    session.delete(entity);
                },
                "serviceId", entity.getId(),
                "serviceName", entity.getName()
        );
    }

    @Override
    public Optional<Services> findByName(String name) {
        return executeWithResult("findByName",
                session -> {
                    String hql = "SELECT s FROM Services s WHERE s.name = :name";
                    return session.createQuery(hql, Services.class)
                            .setParameter("name", name)
                            .uniqueResultOptional();
                },
                "name", name
        );
    }
}