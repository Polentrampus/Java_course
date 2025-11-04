package hotel.controller.manager;

import hotel.model.Hotel;
import hotel.model.filter.FilterClient;
import hotel.model.filter.FilterServices;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.users.client.Client;
import hotel.users.employee.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public void addService(List<Services> nameServices, int idClient) {
        if (hotel.getClientMap().get(idClient) == null) {
            throw new IllegalArgumentException("Клиент с ID " + idClient + " не найден");
        }
        List<Services> curServices = hotel.getClientMap().get(idClient).getServicesList();
        List<Services> newServices = new ArrayList<>();
        for (Services service : curServices) {
            newServices.add(service);
        }
        hotel.getClientMap().get(idClient).setServicesList(curServices);
    }

    public String getInfoAboutClient(int idClient) {
        Client client = hotel.getClientMap().get(idClient);
        Room room = hotel.getRoomMap().get(client.getNumberRoom());
        return String.format(client.toString() + " проживает в комнате %d типа %s категории %s и по цене %d рублей\n", room.getNumber(),
                room.getType(), room.getCategory(), room.getPrice());

    }

    public void getInfoAboutClientDatabase(FilterClient filter){
        System.out.println("Клиенты проживающие в отеле:");
        hotel.getClientMap().values().stream().
                forEach(client -> System.out.println(client.toString()));
    }

    public List<Client> sortClient(FilterClient filter){
        return hotel.getClientMap().values().stream()
                .sorted(filter.getComparator())
                .collect(Collectors.toList());
    }

    public void requestLastThreeClient(){
        System.out.printf("%s сделал запрос на список последних трех человек:\n", employee.getPosition());
        List<Client> clientList = sortClient(FilterClient.ID);
        for (int i = clientList.size() - 1; (clientList.size() - 3 <= 0 ? i <= 0 : i >= clientList.size() - 3); i--){
            System.out.println(clientList.get(i).toString());
        }
    }

    public void requestListServicesClient(FilterServices filter, int idClient){
        if(hotel.getClientMap().get(idClient) == null){
            System.out.println("Такого клиента не существует!");
            return;
        }
        System.out.printf("%s сделал запрос на список услуг %s :\n", employee.getPosition(), hotel.getClientMap().get(idClient).toString());
        for (Services service : hotel.getClientMap().get(idClient).getServicesList()){
            System.out.println(service.toString());
        }
    }
}
