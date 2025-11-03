package hotel.personal.employee;


import hotel.model.Hotel;
import hotel.personal.employee.service.Observer;

import java.time.LocalDate;

public class Admin extends Employee implements Observer {
    protected final Hotel hotel = Hotel.getInstance();

    public Admin(int id, String name, String surname, String patronymic, LocalDate date_of_birth) {
        super(id, name, surname, patronymic, date_of_birth);
        System.out.println("Вы пригласили администратора!");
    }

    @Override
    public String toString() {
        return "Admin{" +
                "name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", patronymic='" + getPatronymic() + '\'' +
                '}';
    }

    @Override
    public String getPosition() {
        return "admin";
    }

    @Override
    public void update(int idRoom) {
        System.out.printf("%s получил информацию о комнате %d\n", getPosition(), idRoom);
    }
}
