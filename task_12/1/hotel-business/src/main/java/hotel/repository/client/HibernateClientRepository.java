package hotel.repository.client;

import hotel.dao.ClientDao;
import hotel.model.users.client.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcClientRepository implements ClientRepository {
    private ClientDao clientDao = ClientDao.getInstance();

    @Override
    public Optional<Client> findById(int id) throws SQLException {
        return clientDao.findById(id);
    }

    @Override
    public List<Client> findAll() {
        return clientDao.findAll();
    }

    @Override
    public boolean save(Client client) {
        clientDao.save(client);
        return true;
    }

    @Override
    public boolean update(Client client) {
        clientDao.update(client);
        return true;
    }

    @Override
    public boolean delete(int id) {
        clientDao.delete(id);
        return true;
    }
}
