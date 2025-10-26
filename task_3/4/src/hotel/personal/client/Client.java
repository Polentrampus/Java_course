package hotel.personal.client;

import hotel.model.controller.manager.ServicesManager;
import hotel.model.service.Services;
import hotel.personal.employee.Person;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class Client extends Person {
    private final int id;
    private List<Services> servicesList;
    private int idRoom;
    LocalDate checkOutDate, departureDate;

    public Client(int id, String name, String surname, String patronymic,
                  LocalDate date_of_birth, List<Services> servicesList, int idRoom,
                  LocalDate checkOutDate, LocalDate departureDate)
    {
        super(id, name, surname, patronymic, date_of_birth);
        System.out.println("Вы пригласили клиента");
        this.id = ++id;
        this.idRoom = idRoom;
        this.servicesList = servicesList;
        this.checkOutDate = checkOutDate;
        this.departureDate = departureDate;
    }

    public int getId() {
        return id;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    public List<Services> getServicesList() {
        return servicesList;
    }

    public void setServicesList(List<Services> servicesList){
        this.servicesList = servicesList;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", patronymic='" + getPatronymic() + '\'' +
                '}';
    }
}
