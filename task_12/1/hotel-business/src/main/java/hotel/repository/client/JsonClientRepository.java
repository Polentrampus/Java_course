package hotel.repository.client;

import hotel.model.users.client.Client;
import hotel.util.JsonDataManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonClientRepository implements ClientRepository {
    private JsonDataManager dataManager = JsonDataManager.getInstance();
    public JsonClientRepository() {
    }

    @Override
    public Optional<Client> findById(int id) throws SQLException {
        return Optional.ofNullable(dataManager.getClients().get(id));
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(dataManager.getClients().values());
    }

    @Override
    public boolean save(Client client) {
        if (client.getId() == null) {
            int maxId = dataManager.getClients().keySet().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            client.setId(maxId + 1);
        }

        dataManager.saveClient(client);
        return true;
    }

    @Override
    public boolean update(Client client) {
        return dataManager.getClients().put(client.getId(), client) != null;
    }

    @Override
    public boolean delete(int id) {
        dataManager.deleteClient(id);
        return true;
    }
}
