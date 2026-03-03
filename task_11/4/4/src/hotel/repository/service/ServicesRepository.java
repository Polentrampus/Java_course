package hotel.repository.service;

import hotel.model.service.Services;
import hotel.repository.HotelRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ServicesRepository extends HotelRepository <Services>{
    Optional<Services> findByName(String name);
}
