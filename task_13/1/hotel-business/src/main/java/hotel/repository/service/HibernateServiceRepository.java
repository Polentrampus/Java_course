package hotel.repository.service;

import hotel.annotation.Component;
import hotel.model.service.Services;

import hotel.repository.BaseRepository;

import java.util.Optional;

@Component
public class HibernateServiceRepository extends BaseRepository<Services, Integer> implements ServicesRepository {
    public HibernateServiceRepository() {
        setEntityClass(Services.class);
    }

    @Override
    public Optional<Services> findByName(String name) {
        String hql = """
                    SELECT s FROM Services s WHERE s.name = :name
            """;
        return getCurrentSession().
                createQuery(hql, Services.class).
                setParameter("name", name).
                uniqueResultOptional();
    }
}