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
    public Client(int id, String name, String surname, String patronymic,LocalDate dateOfBirth)
    {
        super(id, name, surname, patronymic, dateOfBirth);
        System.out.println("Вы пригласили клиента");
    }

    public Client() {}

    @Override
    public String toString() {
        return "Client{" +
                "name=" + super.getName() +
                ", surname=" + super.getSurname() +
                '}';
    }
}
