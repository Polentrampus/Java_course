package hotel.repository.person;

import hotel.model.users.Person;
import hotel.repository.HotelRepository;

public interface PersonRepository<T extends Person> extends HotelRepository<T> {
}