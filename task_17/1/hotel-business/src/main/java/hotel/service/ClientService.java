package hotel.service;

import hotel.dto.CreateClientRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.client.ClientException;
import hotel.exception.client.ClientNotFoundException;
import hotel.exception.dao.DAOException;
import hotel.model.filter.ClientFilter;
import hotel.model.users.client.Client;
import hotel.repository.client.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("clientService")
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public String getInfoAboutClient(Integer idClient) {
        log.info("Getting info about client with id: {}", idClient);

        if (idClient == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID клиента не может быть null");
        }

        try {
            Client client = clientRepository.findById(idClient)
                    .orElseThrow(() -> new ClientNotFoundException(idClient));

            log.info("Client found: {}", client);
            return client.toString();

        } catch (ClientNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while getting client info for id: {}", idClient, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении информации о клиенте", e);
        } catch (Exception e) {
            log.error("Unexpected error while getting client info for id: {}", idClient, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при получении информации о клиенте", e);
        }
    }

    public List<Client> getInfoAboutClientDatabase(ClientFilter filter) {
        log.info("Getting clients with filter: {}", filter);

        try {
            List<Client> clients = sortClient(filter);
            log.info("Found {} clients", clients.size());
            return clients;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting clients with filter: {}", filter, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка клиентов", e);
        }
    }

    public List<Client> sortClient(ClientFilter filter) {
        log.info("Sorting clients with filter: {}", filter);

        if (filter == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Фильтр не может быть null");
        }

        try {
            List<Client> clients = clientRepository.findAll();
            clients.sort(filter.getComparator());
            log.info("Sorted {} clients", clients.size());
            return clients;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while sorting clients with filter: {}", filter, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при сортировке клиентов", e);
        }
    }

    public void requestLastThreeClient() {
        log.info("Requesting last three clients");

        try {
            System.out.println("Сделан запрос на список последних трех человек:");

            List<Client> clientList = sortClient(ClientFilter.ID);

            int startIndex = Math.max(0, clientList.size() - 3);
            for (int i = clientList.size() - 1; i >= startIndex; i--) {
                System.out.println(clientList.get(i).toString());
            }

            log.info("Last three clients request completed");

        } catch (Exception e) {
            log.error("Error while requesting last three clients", e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Ошибка при получении последних трех клиентов", e);
        }
    }

    public Optional<Client> findById(Integer id) {
        log.info("Finding client by id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID клиента не может быть null");
        }

        try {
            Optional<Client> client = clientRepository.findById(id);
            log.info("Client found: {}", client.isPresent());
            return client;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding client by id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске клиента", e);
        }
    }

    public List<Client> findAll() {
        log.info("Finding all clients");

        try {
            List<Client> clients = clientRepository.findAll();
            log.info("Found {} clients", clients.size());
            return clients;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding all clients", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка всех клиентов", e);
        }
    }

    public Optional<Client> save(CreateClientRequest request) {
        log.info("Saving new client: {} {}", request.getName(), request.getSurname());

        validateCreateClientRequest(request);

        try {
            Client client = new Client();
            client.setName(request.getName());
            client.setSurname(request.getSurname());
            client.setPatronymic(request.getPatronymic());
            client.setDateOfBirth(request.getDateOfBirth());

            Integer savedId = clientRepository.save(client);
            log.info("Client saved with id: {}", savedId);

            return Optional.ofNullable(clientRepository.findById(savedId)
                    .orElseThrow(() -> new HotelException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                            "Не удалось найти сохраненного клиента")));

        } catch (DAOException e) {
            log.error("Database error while saving client", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при сохранении клиента", e);
        } catch (Exception e) {
            log.error("Unexpected error while saving client", e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при сохранении клиента", e);
        }
    }

    public void update(Client client) {
        log.info("Updating client with id: {}", client != null ? client.getId() : null);

        if (client == null || client.getId() == 0) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Клиент и его ID не могут быть null");
        }

        try {
            if (clientRepository.findById(client.getId()).isEmpty()) {
                throw new ClientNotFoundException(client.getId());
            }

            clientRepository.update(client);
            log.info("Client updated successfully with id: {}", client.getId());

        } catch (ClientNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while updating client with id: {}", client.getId(), e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при обновлении клиента", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating client with id: {}", client.getId(), e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при обновлении клиента", e);
        }
    }

    public void delete(Integer id) {
        log.info("Deleting client with id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID клиента не может быть null");
        }

        try {
            Client client = clientRepository.findById(id)
                    .orElseThrow(() -> new ClientNotFoundException(id));

            clientRepository.delete(client);
            log.info("Client deleted successfully with id: {}", id);

        } catch (ClientNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while deleting client with id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при удалении клиента", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting client with id: {}", id, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при удалении клиента", e);
        }
    }

    private void validateCreateClientRequest(CreateClientRequest request) {
        if (request == null) {
            throw new ClientException(ErrorCode.VALIDATION_ERROR, "Запрос на создание клиента не может быть null");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ClientException(ErrorCode.VALIDATION_ERROR, "Имя клиента обязательно");
        }
        if (request.getSurname() == null || request.getSurname().trim().isEmpty()) {
            throw new ClientException(ErrorCode.VALIDATION_ERROR, "Фамилия клиента обязательна");
        }
        if (request.getDateOfBirth() == null) {
            throw new ClientException(ErrorCode.VALIDATION_ERROR, "Дата рождения обязательна");
        }
        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new ClientException(ErrorCode.VALIDATION_ERROR, "Дата рождения не может быть в будущем");
        }
    }
}