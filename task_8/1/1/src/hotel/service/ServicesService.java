package hotel.service;

import hotel.exception.service.ServiceAlreadyExistsException;
import hotel.model.Hotel;
import hotel.model.service.Services;
import hotel.model.users.employee.Employee;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
public class ServicesService {
    protected final Hotel hotel = Hotel.getInstance();

    public Collection<Services> requestListServices(){
        if( hotel.getServices().isEmpty()){
            System.out.println("Услуг нет.");
            return Collections.emptyList();
        }
        Collection<Services> servicesList = new ArrayList<>();
        int count = 1;
        servicesList = hotel.getServices().get().values().stream().toList();
        for (Services services : servicesList){
            System.out.printf("%d) %s\n", count, services.toString());
            count++;
        }
        return servicesList;
    }

    public void addService(int id, String name, String description, int price){
        if(hotel.getServices().get().get(name) == null){
            hotel.getServices().get().put(name, new Services(id, name, description, price));
        }
        else
            throw new ServiceAlreadyExistsException(name);
    }

    public void setPrice(String name, int price){
        Services services = hotel.getServices().get().get(name);
        if(services == null){
            System.out.println("Такой услуги не существует!");
            return;
        }
        services.setPrice(price);
    }
}
