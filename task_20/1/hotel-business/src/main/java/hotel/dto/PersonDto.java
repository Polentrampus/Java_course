package hotel.dto;

import hotel.model.users.Person;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonDto {
    private Integer id;
    private String type;
    private String name;
    private String surname;
    private String patronymic;
    private LocalDate dateOfBirth;

    public PersonDto(Person person) {
        this.id = person.getId();
        this.type = person.getType();
        this.name = person.getName();
        this.surname = person.getSurname();
        this.patronymic = person.getPatronymic();
        this.dateOfBirth = person.getDateOfBirth();
    }
}
