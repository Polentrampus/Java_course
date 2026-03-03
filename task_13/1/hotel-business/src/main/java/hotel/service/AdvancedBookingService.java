package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Расширенный класс
 * base класс = BookingService - полная реализация интерфейса IBookingService
 */
@Component("advancedBookingService")
public class AdvancedBookingService implements IBookingService {
    @Inject
    public BookingsRepository bookingsRepository;
    @Inject
    public ClientRepository clientRepository;
    @Inject
    public RoomRepository roomRepository;
    @Inject
    public TransactionManager transactionManager;
    @Inject
    private BookingService bookingService;
    private static final Logger log = LoggerFactory.getLogger(AdvancedBookingService.class);

    public AdvancedBookingService() {
        super();
    }

    @Override
    public List<Bookings> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) throws SQLException {
        return bookingService.getBookingById(id);
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) throws SQLException {
        return bookingService.createBooking(request);
    }

    @Override
    public BigDecimal givOutCheck(Integer idBooking) throws SQLException {
        return bookingService.givOutCheck(idBooking);
    }

    @Override
    public List<Bookings> findActiveByRoomId(Integer idRoom, LocalDate date) {
        return bookingService.findActiveByRoomId(idRoom, date);
    }

    @Override
    public List<Bookings> findActiveByClientId(Integer idClient) {
        return bookingService.findActiveByClientId(idClient);
    }

    public void deleteBookingById(Integer id) {
        log.info("deleteBookingById()");
        transactionManager.executeInTransaction(() -> {
            if (bookingsRepository.findById(id) != null) {
                throw new BookingNotFoundException(id);
            }
            Optional<Bookings> bookings = bookingsRepository.findById(id);
            log.info("deleteBookingById(): booking: " + bookings.get());
            bookingsRepository.delete(bookings.get());
            return null;
        });
    }

    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        log.info("updateBooking()");
        return transactionManager.executeInTransaction(() -> {
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
        });
    }

    @Override
    public List<Bookings> findByRoomId(Integer roomNumber) {
        return bookingService.findByRoomId(roomNumber);
    }

    @Override
    public List<Bookings> findByClientId(Integer clientId) {
        return bookingService.findByClientId(clientId);
    }
}
