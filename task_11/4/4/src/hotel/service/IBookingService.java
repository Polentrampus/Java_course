package hotel.service;

import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.model.users.client.Client;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface IBookingService {
    List<Bookings> getAllBookings();
    Optional<Bookings> getBookingById(Integer id);
    Optional<Bookings> getBookingByIdClient(Integer idClient);
    Optional<Bookings> createBooking(CreateBookingRequest request);
    void deleteBookingById(Integer id);
    Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking);
    double givOutCheck(int idClient);
}
