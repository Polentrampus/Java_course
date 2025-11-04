package hotel.users.client;

import hotel.model.service.Services;
import hotel.users.employee.Person;

import java.time.LocalDate;
import java.util.List;

public class Client extends Person {
    private List<Services> servicesList;
    private int numberRoom;
    LocalDate checkOutDate, departureDate;

    public Client(int id, String name, String surname, String patronymic,
                  LocalDate date_of_birth, List<Services> servicesList, int idRoom,
                  LocalDate checkOutDate, LocalDate departureDate)
    {
        super(id, name, surname, patronymic, date_of_birth);
        System.out.println("Вы пригласили клиента");
        this.numberRoom = idRoom;
        this.servicesList = servicesList;
        this.checkOutDate = checkOutDate;
        this.departureDate = departureDate;
    }

    public int getId() {
        return super.getId();
    }

    public int getNumberRoom() {
        return numberRoom;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setIdRoom(int idRoom) {
        this.numberRoom = idRoom;
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
                "name=" + super.getName() +
                ", patronymic=" + super.getPatronymic() +
                ", surname=" + super.getSurname() +
                ", servicesList=" + servicesList +
                ", numberRoom=" + numberRoom +
                ", checkOutDate=" + checkOutDate +
                ", departureDate=" + departureDate +
                '}';
    }
}
