package hotel.controller.manager;

import hotel.model.Hotel;
import hotel.model.service.Services;
import hotel.users.employee.Employee;

import java.util.List;

public class ServicesManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public ServicesManager(Employee employee) {
        this.employee = employee;
        for (Services service : Services.values()) {
            hotel.getSERVICES().put(service.name(), service);
        }
    }

    public List<Services> requestListServices(){
        int count = 1;
        for (Services services : Hotel.getInstance().getSERVICES().values()){
            System.out.printf("%d) %s\n", count, services.toString());
            count++;
        }
        return (List<Services>) Hotel.getInstance().getSERVICES().values();
    }
}
