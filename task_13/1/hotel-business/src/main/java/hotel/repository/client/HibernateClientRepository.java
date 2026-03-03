package hotel.repository.client;

import hotel.annotation.Component;
import hotel.model.users.client.Client;
import hotel.repository.BaseRepository;
import hotel.repository.HotelRepository;

@Component
public class HibernateClientRepository extends BaseRepository<Client, Integer> implements ClientRepository {
    public HibernateClientRepository() {
        setEntityClass(Client.class);
    }
}
