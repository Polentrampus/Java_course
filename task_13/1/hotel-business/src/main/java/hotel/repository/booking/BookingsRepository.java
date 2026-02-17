package hotel.repository.booking;

import hotel.model.booking.Bookings;
import hotel.model.service.Services;
import hotel.repository.HotelRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingsRepository extends HotelRepository<Bookings> {
    List<Bookings> findActiveBookings(LocalDate date);
    List<Bookings> findByRoomId(Integer roomId);
    List<Bookings> findByClientId(Integer clientId);
    List<Bookings> findActiveByRoomId(Integer roomId, LocalDate date);
    List<Services> getBookingServices(Integer bookingId);
    Optional<Bookings> addBookingServices(Integer bookingId, List<Integer> serviceIds);
    Optional<Bookings> removeBookingServices(Integer bookingId, List<Integer> serviceIds);
}
