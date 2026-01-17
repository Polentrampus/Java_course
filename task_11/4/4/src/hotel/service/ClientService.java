package hotel.service;

import hotel.annotation.Component;
import hotel.exception.client.ClientNotFoundException;
import hotel.model.Hotel;
import hotel.model.booking.Bookings;
import hotel.model.filter.ClientFilter;
import hotel.model.filter.ServicesFilter;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.model.users.employee.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientService {
    protected final Hotel hotel = Hotel.getInstance();

    public ClientService() {
    }

    public List<Services> getServices(int id){
        if(hotel.getClient(id).isEmpty() || hotel.getBookingsMap().isEmpty()){
            return new ArrayList<>();
        }
        for (Bookings booking : hotel.getBookingsMap().get().values()) {
            if(booking.getClient().getId() == id)
                return booking.getServices();
        }
        return new ArrayList<>();
    }

    public void addService(List<Services> nameServices, int idClient) {
        if(hotel.getClient(idClient).isEmpty()){
            System.out.println("Информация о данном клиенте не найдена: ID = "+ idClient);
            return;
        }
        for (Bookings booking : hotel.getBookingsMap().get().values()) {
            if(booking.getClient().getId() == idClient)
                booking.setServices(nameServices);
        }
    }

    public String getInfoAboutClient(int idClient) {
        if( hotel.getClientMap().isEmpty()){
            System.out.println("Постояльцев нет");
            return "";
        }
        if(hotel.getClientMap().get().get(idClient) == null){
            throw new ClientNotFoundException(idClient);
        }
        Client client = hotel.getClientMap().get().get(idClient);
        Bookings bookings = new Bookings();
        for (Bookings booking : hotel.getBookingsMap().get().values()) {
            if(booking.getClient().getId() == idClient) {
                bookings = booking;
            }
        }
        Room room = bookings.getRoom();
        return String.format(client.toString() + " проживает в комнате %d типа %s категории %s и по цене %d рублей\n", room.getNumber(),
                room.getType(), room.getCategory(), room.getPrice());

    }

    public List<Client> getInfoAboutClientDatabase(ClientFilter filter){
        if( hotel.getClientMap().isEmpty()){
            System.out.println("Постояльцев нет");
            return Collections.emptyList();
        }
        System.out.println("Клиенты проживающие в отеле:");
        hotel.getClientMap().get().values().
                forEach(client -> System.out.println(client.toString()));
        return hotel.getClientMap().get().values().stream().toList();
    }

    public List<Client> sortClient(ClientFilter filter){
        if( hotel.getClientMap().isEmpty()){
            System.out.println("Постояльцев нет");
            return Collections.emptyList();
        }
        return hotel.getClientMap().get().values().stream()
                .sorted(filter.getComparator())
                .collect(Collectors.toList());
    }

    public void requestLastThreeClient(){
        System.out.println("Сделан запрос на список последних трех человек:");
        List<Client> clientList = sortClient(ClientFilter.ID);

        for (int i = clientList.size() - 1; (clientList.size() - 3 <= 0 ? i <= 0 : i >= clientList.size() - 3); i--){
            System.out.println(clientList.get(i).toString());
        }
    }

    public void requestListServicesClient(ServicesFilter filter, int idClient){
        if( hotel.getClientMap().isEmpty()){
            System.out.println("Постояльцев нет");
            return;
        }
        if(hotel.getClientMap().get().get(idClient) == null){
            throw new ClientNotFoundException(idClient);
        }
        System.out.println("Сделан запрос на список услуг клиента с ID " + hotel.getClientMap().get().get(idClient).toString());
        List<Services> servicesList = getServices(idClient);
        for (Services service : servicesList){
            System.out.println(service.toString());
        }
    }
}
