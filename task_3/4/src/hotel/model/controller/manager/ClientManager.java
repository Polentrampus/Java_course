package hotel.model.controller.manager;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.personal.client.Client;
import hotel.personal.employee.Employee;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public void addService(List<Services> nameServices, int idClient) {
        if (hotel.getClientMap().get(idClient) == null) {
            throw new IllegalArgumentException("Клиент с ID " + idClient + " не найден");
        }
        List<Services> curServices = hotel.getClientMap().get(idClient).getServicesList();
        List<Services> newServices = new ArrayList<>();
        for(Services service : curServices){
            newServices.add(service);
        }
        hotel.getClientMap().get(idClient).setServicesList(curServices);
    }

    public String getInfoAboutClient(int idClient){
        Client client = hotel.getClientMap().get(idClient);
        Room room = hotel.getRoomMap().get(client.getIdRoom());
        return String.format(client.toString() + " проживает в комнате %d типа %s категории %s и по цене %d рублей\n", room.getId(),
                room.getType(), room.getCategory(), room.getPrice());

    }
}
