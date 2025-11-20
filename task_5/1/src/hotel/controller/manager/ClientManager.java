package hotel.controller.manager;

import hotel.model.Hotel;
import hotel.model.filter.ClientFilter;
import hotel.model.filter.ServicesFilter;
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

    public ClientManager(Employee employee) {
        this.employee = employee;
    }

    public List<Services> getServices(int id){
        if(hotel.getClient(id).isEmpty()){
            return new ArrayList<>();
        }
        return hotel.getClient(id).get().getServicesList();
    }

    public void addService(List<Services> nameServices, int idClient) {
        if(hotel.getClient(idClient).isEmpty()){
            System.out.println("Информация о данном клиенте не найдена: ID = "+ idClient);
            return;
        }
        hotel.getClient(idClient).get().setServicesList(nameServices);
    }

    public String getInfoAboutClient(int idClient) {
        Client client = hotel.getClientMap().get().get(idClient);
        if(client == null){
            System.out.println("Информация о данном клиенте не найдена: ID = "+ idClient);
            return "";
        }
        Room room = hotel.getRoomMap().get().get(client.getNumberRoom());
        return String.format(client.toString() + " проживает в комнате %d типа %s категории %s и по цене %d рублей\n", room.getNumber(),
                room.getType(), room.getCategory(), room.getPrice());

    }

    public void getInfoAboutClientDatabase(ClientFilter filter){
        System.out.println("Клиенты проживающие в отеле:");
        hotel.getClientMap().get().values().stream().
                forEach(client -> System.out.println(client.toString()));
    }

    public List<Client> sortClient(ClientFilter filter){
        return hotel.getClientMap().get().values().stream()
                .sorted(filter.getComparator())
                .collect(Collectors.toList());
    }

    public void requestLastThreeClient(){
        System.out.printf("%s сделал запрос на список последних трех человек:\n", employee.getPosition());
        List<Client> clientList = sortClient(ClientFilter.ID);
        if(clientList.isEmpty()) {
            System.out.println("к сожалению список пуст(");
            return;
        }
        for (int i = clientList.size() - 1; (clientList.size() - 3 <= 0 ? i <= 0 : i >= clientList.size() - 3); i--){
            System.out.println(clientList.get(i).toString());
        }
    }

    public void requestListServicesClient(ServicesFilter filter, int idClient){
        if(hotel.getClientMap().get().get(idClient) == null){
            System.out.println("Такого клиента не существует!");
            return;
        }
        System.out.printf("%s сделал запрос на список услуг %s :\n", employee.getPosition(), hotel.getClientMap().get().get(idClient).toString());
        for (Services service : hotel.getClientMap().get().get(idClient).getServicesList()){
            System.out.println(service.toString());
        }
    }
}
