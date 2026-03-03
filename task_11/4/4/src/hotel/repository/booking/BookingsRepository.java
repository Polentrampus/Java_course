package hotel.repository.booking;

import hotel.model.booking.Bookings;
import hotel.repository.HotelRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingsRepository extends HotelRepository<Bookings> {
    List<Bookings> findActiveBookings(LocalDate date);
    List<Bookings> findByRoomId(int roomId);
    List<Bookings> findByClientId(int clientId);
    Optional<Bookings> findActiveByRoomId(int roomId, LocalDate date);
}
