package hotel.repository.person;

import hotel.model.users.Person;
import hotel.model.users.client.Client;
import hotel.repository.BaseRepository;
import hotel.repository.client.ClientRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HibernatePersonRepository extends BaseRepository<Person, Integer> implements PersonRepository {

    public HibernatePersonRepository() {
        setEntityClass(Person.class);
    }

    @Override
    public Optional<Person> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(Person.class, id)),
                "id", id, "entity", "Person"
        );
    }

    @Override
    public List<Person> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from Person", Person.class).list(),
                "entity", "Person"
        );
    }

    @Override
    public Integer save(Person entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "personName", entity.getName(),
                "personSurname", entity.getSurname()
        );
    }

    @Override
    public void update(Person entity) {
        execute("update",
                session -> session.update(entity),
                "personId", entity.getId(),
                "personName", entity.getName()
        );
    }

    @Override
    public void delete(Person entity) {
        execute("delete",
                session -> {
                    Long bookingCount = (Long) session.createQuery(
                                    "select count(p) from Person p where p.id = :personId")
                            .setParameter("personId", entity.getId())
                            .uniqueResult();

                    session.delete(entity);
                },
                "personId", entity.getId(),
                "personName", entity.getName()
        );
    }
}