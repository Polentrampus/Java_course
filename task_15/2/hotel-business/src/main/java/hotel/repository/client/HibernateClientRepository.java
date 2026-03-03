package hotel.repository.client;

import hotel.model.users.client.Client;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateClientRepository extends BaseRepository<Client, Integer> implements ClientRepository {
    public HibernateClientRepository() {
        setEntityClass(Client.class);
    }
}
