package hotel.personal.employee.service;

import hotel.personal.Observer;
import hotel.personal.Person;

public abstract class ServicePersonnelHotel extends Person implements Observer {
    public ServicePersonnelHotel(String name, String surname, String patronymic, int date_of_birth, int month_of_birth, int year_of_birth) {
        super(name, surname, patronymic, date_of_birth, month_of_birth, year_of_birth);
    }

    @Override
    public abstract void update(int roomId);
}
