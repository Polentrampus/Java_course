package hotel.service;

import hotel.dto.CreateClientRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.repository.client.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service("clientService")
@Transactional
public class ClientService {
    private ClientRepository clientRepository;
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientService() {
    }

    public String getInfoAboutClient(Integer idClient) throws SQLException {
        log.info("getInfoAboutClient() for id: {}", idClient);
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new HotelException(ErrorCode.CLIENT_NOT_FOUND,
                        "Клиент не найден с ID: " + idClient));

        log.info("getInfoAboutClient(): client found: {}", client);
        return client.toString();
    }

    public List<Client> getInfoAboutClientDatabase(ClientFilter filter) {
        log.info("getInfoAboutClientDatabase() with filter: {}", filter);
        List<Client> clients = sortClient(filter);
        log.info("getInfoAboutClientDatabase(): found {} clients", clients.size());
        return clients;
    }

    public List<Client> sortClient(ClientFilter filter) {
        log.info("sortClient() with filter: {}", filter);
        List<Client> clients = clientRepository.findAll();
        clients.sort(filter.getComparator());
        log.info("sortClient(): sorted {} clients", clients.size());
        return clients;
    }

    public void requestLastThreeClient() {
        log.info("requestLastThreeClient()");
        System.out.println("Сделан запрос на список последних трех человек:");

        List<Client> clientList = sortClient(ClientFilter.ID);

        int startIndex = Math.max(0, clientList.size() - 3);
        for (int i = clientList.size() - 1; i >= startIndex; i--) {
            System.out.println(clientList.get(i).toString());
        }

        log.info("requestLastThreeClient(): completed");
    }

    public Optional<Client> findById(Integer id) throws SQLException {
        log.info("findById() for id: {}", id);
        Optional<Client> client = clientRepository.findById(id);
        log.info("findById(): client found: {}", client.isPresent());
        return client;
    }

    public List<Client> findAll() {
        log.info("findAll()");
        List<Client> clients = clientRepository.findAll();
        log.info("findAll(): found {} clients", clients.size());
        return clients;
    }

    public Optional<Client> save(CreateClientRequest request) throws SQLException {
        log.info("save() for client: {}", request.getName());
        Client client = new Client();
        client.setName(request.getName());
        client.setSurname(request.getSurname());
        client.setPatronymic(request.getPatronymic());
        client.setDateOfBirth(request.getDateOfBirth());

        Integer result = clientRepository.save(client);
        log.info("save(): client saved with id: {}", result);
        return findById(result);
    }

    public void update(Client client) throws SQLException {
        log.info("update() for client: {}", client);
        if (!clientRepository.findById(client.getId()).isPresent()) {
            throw new HotelException(ErrorCode.CLIENT_NOT_FOUND,
                    "Клиент не найден для обновления");
        }

        clientRepository.update(client);
        log.info("update(): result: {}", true);
    }

    public void delete(Integer id) throws SQLException {
        log.info("delete() for id: {}", id);
        Optional<Client> client = clientRepository.findById(id);
        clientRepository.delete(client.get());
        log.info("delete(): result: {}", true);
    }
}