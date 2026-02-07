package hotel.model.users.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.service.export_import.Entity;
import hotel.model.service.Services;
import hotel.model.users.Person;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Client extends Person implements Entity  {
    @JsonIgnore
    private List<Services> servicesList;
    private int numberRoom;
    private LocalDate checkOutDate;
    private LocalDate departureDate;

    public Client(int id, String name, String surname, String patronymic,
                  LocalDate dateOfBirth, List<Services> servicesList, int idRoom,
                  LocalDate checkOutDate, LocalDate departureDate)
    {
        super(id, name, surname, patronymic, dateOfBirth);
        System.out.println("Вы пригласили клиента");
        this.numberRoom = idRoom;
        this.servicesList = servicesList != null ? new ArrayList<>(servicesList) : new ArrayList<>();
        this.checkOutDate = checkOutDate;
        this.departureDate = departureDate;
    }

    public Client() {

    }

    public List<Services> getServicesList() {
        return servicesList;
    }

    public void setServicesList(List<Services> servicesList) {
        this.servicesList = servicesList;
    }

    public int getNumberRoom() {
        return numberRoom;
    }

    public void setNumberRoom(int numberRoom) {
        this.numberRoom = numberRoom;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name=" + super.getName() +
                ", surname=" + super.getSurname() +
                ", numberRoom=" + numberRoom +
                '}';
    }
}
