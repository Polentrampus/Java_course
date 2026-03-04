package hotel.repository.client;

import hotel.model.users.client.Client;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HibernateClientRepository extends BaseRepository<Client, Integer> implements ClientRepository {

    public HibernateClientRepository() {
        setEntityClass(Client.class);
    }

    @Override
    public Optional<Client> findById(Integer id) {
        return executeWithResult("findById",
                session -> Optional.ofNullable(session.get(Client.class, id)),
                "id", id, "entity", "Client"
        );
    }

    @Override
    public List<Client> findAll() {
        return executeWithResult("findAll",
                session -> session.createQuery("from Client", Client.class).list(),
                "entity", "Client"
        );
    }

    @Override
    public Integer save(Client entity) {
        return executeWithResult("save",
                session -> (Integer) session.save(entity),
                "clientName", entity.getName(),
                "clientSurname", entity.getSurname()
        );
    }

    @Override
    public void update(Client entity) {
        execute("update",
                session -> session.update(entity),
                "clientId", entity.getId(),
                "clientName", entity.getName()
        );
    }

    @Override
    public void delete(Client entity) {
        execute("delete",
                session -> {
                    Long bookingCount = (Long) session.createQuery(
                                    "select count(b) from Bookings b where b.client.id = :clientId")
                            .setParameter("clientId", entity.getId())
                            .uniqueResult();

                    session.delete(entity);
                },
                "clientId", entity.getId(),
                "clientName", entity.getName()
        );
    }
}