package hotel.model.users.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.controller.export_import.Entity;
import hotel.model.service.Services;
import hotel.model.users.Person;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        return "Client{" +
                "name=" + super.getName() +
                ", surname=" + super.getSurname() +
                ", numberRoom=" + numberRoom +
                '}';
    }
}
