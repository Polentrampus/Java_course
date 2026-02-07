package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.client.ClientNotFoundException;
import hotel.model.booking.Bookings;
import hotel.model.filter.ClientFilter;
import hotel.model.filter.ServicesFilter;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.repository.HotelRepository;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientService implements ClientRepository {
    private ClientRepository clientRepository;
    private BookingsRepository bookingsRepository;

    public void setHotelRepository(ClientRepository clientRepository, BookingsRepository bookingsRepository) {
        this.clientRepository = clientRepository;
        this.bookingsRepository = bookingsRepository;
    }

    public ClientService() {
    }

    public List<Services> getServicesByIdBooking(int id) throws SQLException {
        try{
            return bookingsRepository.findById(id).get().getServices();
        }catch (SQLException e){
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,e.getMessage());
        }
    }

    public String getInfoAboutClient(int idClient) throws SQLException {
        try{
            Client client = clientRepository.findById(idClient).get();
            return client.toString();
        }catch (SQLException e){
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,e.getMessage());
        }
    }

    public List<Client> getInfoAboutClientDatabase(ClientFilter filter) {
        try{
            return sortClient(filter);
        }catch (Exception e){
            throw new HotelException(ErrorCode.CLIENT,e.getMessage());
        }
    }

    public List<Client> sortClient(ClientFilter filter) {
        try{
            List<Client> clients = clientRepository.findAll();
            clients.sort(filter.getComparator());
            return clients;
        }catch (Exception e){
            throw new HotelException(ErrorCode.CLIENT,e.getMessage());
        }

    }

    public void requestLastThreeClient() {
        System.out.println("Сделан запрос на список последних трех человек:");
        List<Client> clientList = sortClient(ClientFilter.ID);

        for (int i = clientList.size() - 1; (clientList.size() - 3 <= 0 ? i <= 0 : i >= clientList.size() - 3); i--) {
            System.out.println(clientList.get(i).toString());
        }
    }

    @Override
    public Optional<Client> findById(int id) throws SQLException {
        return clientRepository.findById(id);
    }

    @Override
    public List<Client> findAll() {
        try{
            return clientRepository.findAll();
        }catch (Exception e){
            throw new HotelException(ErrorCode.CLIENT_NOT_FOUND,e.getMessage());
        }
    }

    @Override
    public boolean save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public boolean update(Client client) {
        return clientRepository.update(client);
    }

    @Override
    public boolean delete(int id) {
        return clientRepository.delete(id);
    }
}
