package hotel.model.users.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hotel.model.users.Person;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
@DiscriminatorValue("client")
public class Client extends Person{
    private String notes;

    public Client(int id, String name, String surname, String patronymic, LocalDate dateOfBirth, String notes) {
        super(id, "client", name, surname, patronymic, dateOfBirth);
        this.notes = notes;
        System.out.println("Вы пригласили клиента");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Client() {
    }

    @Override
    public String toString() {
        return "Client{" +
                "name=" + super.getName() +
                ", surname=" + super.getSurname() +
                '}';
    }
}
