package hotel.repository.client;

import hotel.model.room.Room;
import hotel.model.users.client.Client;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class InMemoryClientRepository implements ClientRepository{
    private Map<Integer, Client> clientMap = new HashMap<>();

    public InMemoryClientRepository() {
     initializeTestData();
    }
    private void initializeTestData() {
        clientMap.put(1, new Client(1, "Анна", "Смирнова", "Игоревна",
                LocalDate.of(1990, 3, 12), null));

        clientMap.put(2, new Client(2, "Дмитрий", "Козлов", "Анатольевич",
                LocalDate.of(1985, 7, 25), null));

        clientMap.put(3, new Client(3, "Светлана", "Попова", "Владимировна",
                LocalDate.of(1993, 11, 8), null));

        clientMap.put(4, new Client(4, "Михаил", "Орлов", "Сергеевич",
                LocalDate.of(1978, 5, 30), null));

        clientMap.put(5, new Client(5, "Екатерина", "Новикова", "Александровна",
                LocalDate.of(1988, 9, 14), null));

    }

        @Override
    public Optional<Client> findById(int id) throws SQLException {
        return Optional.ofNullable(clientMap.get(id));
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clientMap.values());
    }

    @Override
    public boolean save(Client client) {
        clientMap.put(client.getId(), client);
        return true;
    }

    @Override
    public boolean update(Client client) {
        clientMap.put(client.getId(), client);
        return true;
    }

    @Override
    public boolean delete(int id) {
        clientMap.remove(id);
        return true;
    }
}
