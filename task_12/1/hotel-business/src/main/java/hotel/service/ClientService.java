package hotel.service;

import hotel.annotation.Component;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.filter.ClientFilter;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class ClientService implements ClientRepository {
    private ClientRepository clientRepository;
    private BookingsRepository bookingsRepository;
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    public void setHotelRepository(ClientRepository clientRepository, BookingsRepository bookingsRepository) {
        this.clientRepository = clientRepository;
        this.bookingsRepository = bookingsRepository;
    }

    public ClientService() {
    }

    public List<Services> getServicesByIdBooking(int id) throws SQLException {
        log.info("getServicesByIdBooking()");
        try {
            List<Services> services = bookingsRepository.findById(id).get().getServices();
            log.info("getServicesByIdBooking(): services size: " + services.size());
            return services;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR, e.getMessage());
        }
    }

    public String getInfoAboutClient(int idClient) throws SQLException {
        log.info("getInfoAboutClient()");
        try {
            Client client = clientRepository.findById(idClient).get();
            log.info("getInfoAboutClient(): client: " + client);
            return client.toString();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR, e.getMessage());
        }
    }

    public List<Client> getInfoAboutClientDatabase(ClientFilter filter) {
        log.info("getInfoAboutClientDatabase()");
        try {
            List<Client> clients = sortClient(filter);
            log.info("getInfoAboutClientDatabase(): clients size: " + clients.size());
            return clients;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.CLIENT, e.getMessage());
        }
    }

    public List<Client> sortClient(ClientFilter filter) {
        log.info("sortClient()");
        try {
            List<Client> clients = clientRepository.findAll();
            clients.sort(filter.getComparator());
            log.info("sortClient(): sorted clients size: " + clients.size());
            return clients;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.CLIENT, e.getMessage());
        }
    }

    public void requestLastThreeClient() {
        log.info("requestLastThreeClient()");
        System.out.println("Сделан запрос на список последних трех человек:");
        List<Client> clientList = sortClient(ClientFilter.ID);

        for (int i = clientList.size() - 1; (clientList.size() - 3 <= 0 ? i <= 0 : i >= clientList.size() - 3); i--) {
            System.out.println(clientList.get(i).toString());
        }
        log.info("requestLastThreeClient(): completed");
    }

    @Override
    public Optional<Client> findById(int id) throws SQLException {
        log.info("findById()");
        Optional<Client> client = clientRepository.findById(id);
        log.info("findById(): client: " + client);
        return client;
    }

    @Override
    public List<Client> findAll() {
        log.info("findAll()");
        try {
            List<Client> clients = clientRepository.findAll();
            log.info("findAll(): clients size: " + clients.size());
            return clients;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.CLIENT_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public boolean save(Client client) {
        log.info("save()");
        boolean result = clientRepository.save(client);
        log.info("save(): result: " + result);
        return result;
    }

    @Override
    public boolean update(Client client) {
        log.info("update()");
        boolean result = clientRepository.update(client);
        log.info("update(): result: " + result);
        return result;
    }

    @Override
    public boolean delete(int id) {
        log.info("delete()");
        boolean result = clientRepository.delete(id);
        log.info("delete(): result: " + result);
        return result;
    }
}
