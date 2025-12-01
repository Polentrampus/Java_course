package hotel.controller.manager;

import hotel.model.Hotel;
import hotel.model.service.Services;
import hotel.users.employee.Employee;

import java.util.*;

public class ServicesManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public ServicesManager(Employee employee) {
        this.employee = employee;
    }

    public Collection<Services> requestListServices(){
        Collection<Services> servicesList = new ArrayList<>();
        int count = 1;
        servicesList = hotel.getServices().get().values().stream().toList();
        for (Services services : servicesList){
            System.out.printf("%d) %s\n", count, services.toString());
            count++;
        }
        return servicesList;
    }

    public void addService(String name, String description, int price){
        if(hotel.getServices().get().get(name) == null){
            hotel.getServices().get().put(name, new Services(name, description, price));
        }
        else
            System.out.println("Такая услуга уже есть!");
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
