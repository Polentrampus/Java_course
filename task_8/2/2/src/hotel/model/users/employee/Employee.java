package hotel.model.users.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import hotel.service.export_import.Entity;
import hotel.model.Hotel;
import hotel.model.users.Person;
import hotel.model.users.employee.service.Maid;
import hotel.model.users.employee.service.Mender;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Admin.class, name = "admin"),
        @JsonSubTypes.Type(value = Maid.class, name = "maid"),
        @JsonSubTypes.Type(value = Mender.class, name = "mender")
})
public abstract class Employee extends Person implements Entity {
    @JsonIgnore
    protected final Hotel hotel = Hotel.getInstance();
    public abstract String getPosition();

    public Employee(int id, String name, String surname, String patronymic, LocalDate date_of_birth) {
        super(id, name, surname, patronymic, date_of_birth);
    }

    public static Employee createEmployee(int id, String name, String surname,
                                          String patronymic, LocalDate date_of_birth,
                                          String position) {
        return switch(position.toLowerCase()) {
            case "admin" -> new Admin(id, name, surname, patronymic, date_of_birth);
            case "maid" -> new Maid(id, name, surname, patronymic, date_of_birth);
            case "mender" -> new Mender(id, name, surname, patronymic, date_of_birth);
            default -> throw new IllegalArgumentException("Unknown position: " + position);
        };
    }
}
