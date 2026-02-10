package hotel.model.users.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.service.export_import.Entity;
import hotel.model.users.Person;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Client extends Person implements Entity  {
    private String notes;
    public Client(int id, String name, String surname, String patronymic, LocalDate dateOfBirth, String notes)
    {
        super(id, name, surname, patronymic, dateOfBirth);
        this.notes = notes;
        System.out.println("Вы пригласили клиента");
    }

    public String getNotes()
    {
        return notes;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
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
