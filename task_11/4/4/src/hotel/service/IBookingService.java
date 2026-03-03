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
    boolean deleteBookingById(Integer id);
    Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) throws SQLException;
    BigDecimal givOutCheck(int idBooking) throws SQLException;
    Optional<Bookings> findActiveByRoomId(int idRoom, LocalDate date);
    Optional<Bookings> findActiveByClientId(int idClient);
}
