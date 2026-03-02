package hotel.model.users.client;

import hotel.model.users.Person;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
