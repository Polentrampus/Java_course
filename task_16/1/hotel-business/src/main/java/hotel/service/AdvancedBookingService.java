package hotel.service;

import hotel.dto.CreateBookingRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.users.client.Client;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Расширенный класс
 * base класс = BookingService - полная реализация интерфейса IBookingService
 */
@Transactional
public class AdvancedBookingService extends BookingService implements IBookingService {
    private static final Logger log = LoggerFactory.getLogger(AdvancedBookingService.class);

    public AdvancedBookingService(BookingsRepository bookingsRepository,
                                  ClientRepository clientRepository,
                                  RoomRepository roomRepository) {
        super(bookingsRepository, clientRepository, roomRepository);
    }

    public void deleteBookingById(Integer id) throws SQLException {
        log.info("deleteBookingById()");
        Optional<Bookings> bookings = bookingsRepository.findById(id);
        if (bookings.isEmpty()) {
            throw new BookingNotFoundException(id);
        }
        log.info("deleteBookingById(): booking: " + bookings.get());
        bookingsRepository.delete(bookings.get());
    }

    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) throws SQLException {
        log.info("updateBooking()");
        Optional<Bookings> bookings = bookingsRepository.findById(idBooking);
        Optional<Room> room = roomRepository.findById(request.getRoomId());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (bookings == null || room == null || client == null) {
            throw new HotelException(ErrorCode.DATABASE_CONNECTION_ERROR);
        }
        bookings.get().setServices(request.getServices());
        bookings.get().setRoom(room.get());
        bookings.get().setClient(client.get());
        bookings.get().setCheckInDate(request.getCheckInDate());
        bookings.get().setCheckOutDate(request.getCheckOutDate());

        bookingsRepository.update(bookings.get());
        return bookings;
    }
}
