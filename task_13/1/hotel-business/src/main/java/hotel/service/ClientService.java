package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.repository.client.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Component
public class ClientService {
    @Inject
    private ClientRepository clientRepository;

    @Inject
    private TransactionManager transactionManager;

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    public ClientService() {
    }

    public String getInfoAboutClient(Integer idClient) {
        log.info("getInfoAboutClient() for id: {}", idClient);

        return transactionManager.executeInTransaction(() -> {
            Client client = clientRepository.findById(idClient)
                    .orElseThrow(() -> new HotelException(ErrorCode.CLIENT_NOT_FOUND,
                            "Клиент не найден с ID: " + idClient));

            log.info("getInfoAboutClient(): client found: {}", client);
            return client.toString();
        });
    }

    public List<Client> getInfoAboutClientDatabase(ClientFilter filter) {
        log.info("getInfoAboutClientDatabase() with filter: {}", filter);

        return transactionManager.executeInTransaction(() -> {
            List<Client> clients = sortClient(filter);
            log.info("getInfoAboutClientDatabase(): found {} clients", clients.size());
            return clients;
        });
    }

    public List<Client> sortClient(ClientFilter filter) {
        log.info("sortClient() with filter: {}", filter);

        return transactionManager.executeInTransaction(() -> {
            List<Client> clients = clientRepository.findAll();
            clients.sort(filter.getComparator());
            log.info("sortClient(): sorted {} clients", clients.size());
            return clients;
        });
    }

    public void requestLastThreeClient() {
        log.info("requestLastThreeClient()");

        transactionManager.executeInTransaction(() -> {
            System.out.println("Сделан запрос на список последних трех человек:");

            List<Client> clientList = sortClient(ClientFilter.ID);

            int startIndex = Math.max(0, clientList.size() - 3);
            for (int i = clientList.size() - 1; i >= startIndex; i--) {
                System.out.println(clientList.get(i).toString());
            }

            log.info("requestLastThreeClient(): completed");
            return null;
        });
    }

    public Optional<Client> findById(Integer id) {
        log.info("findById() for id: {}", id);

        return transactionManager.executeInTransaction(() -> {
            Optional<Client> client = clientRepository.findById(id);
            log.info("findById(): client found: {}", client.isPresent());
            return client;
        });
    }

    public List<Client> findAll() {
        log.info("findAll()");

        return transactionManager.executeInTransaction(() -> {
            List<Client> clients = clientRepository.findAll();
            log.info("findAll(): found {} clients", clients.size());
            return clients;
        });
    }

    public Integer save(Client client) {
        log.info("save() for client: {}", client);

        return transactionManager.executeInTransaction(() -> {
            Integer result = clientRepository.save(client);
            log.info("save(): client saved with id: {}", result);
            return result;
        });
    }

    public void update(Client client) {
        log.info("update() for client: {}", client);

        transactionManager.executeInTransaction(() -> {
            if (!clientRepository.findById(client.getId()).isPresent()) {
                throw new HotelException(ErrorCode.CLIENT_NOT_FOUND,
                        "Клиент не найден для обновления");
            }

            clientRepository.update(client);
            log.info("update(): result: {}", true);
            return null;
        });
    }

    public void delete(Integer id) {
        log.info("delete() for id: {}", id);

        transactionManager.executeInTransaction(() -> {
            Optional<Client> client = clientRepository.findById(id);
            clientRepository.delete(client.get());
            log.info("delete(): result: {}", true);
            return null;
        });
    }
}