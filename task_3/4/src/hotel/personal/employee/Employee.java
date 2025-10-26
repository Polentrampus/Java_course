package hotel.personal.employee;

import hotel.model.Hotel;
import hotel.personal.employee.service.Maid;
import hotel.personal.employee.service.Mender;


import java.time.LocalDate;

public abstract class Employee extends Person {
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
