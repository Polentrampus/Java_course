package hotel.service;

import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IBookingService {
    List<Bookings> getAllBookings();
    Optional<Bookings> getBookingById(Integer id) throws SQLException;
    Optional<Bookings> createBooking(CreateBookingRequest request) throws SQLException;
    BigDecimal givOutCheck(Integer idBooking) throws SQLException;
    List<Bookings> findActiveByRoomId(Integer idRoom, LocalDate date);
    List<Bookings> findActiveByClientId(Integer idClient);
    void deleteBookingById(Integer id) throws SQLException;
    Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) throws SQLException;
    List<Bookings> findByRoomId(Integer roomNumber);
    List<Bookings> findByClientId(Integer clientId);
    List<Bookings> findActiveBookings(LocalDate localDate);
    Optional<Bookings> addServiceToBooking(Integer bookingId, List<Integer> serviceId) throws SQLException;
}
