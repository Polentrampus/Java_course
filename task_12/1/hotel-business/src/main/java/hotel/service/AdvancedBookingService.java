package hotel.service;

import hotel.annotation.Component;
import hotel.dto.CreateBookingRequest;
import hotel.exception.ErrorCode;
import hotel.exception.SqlException;
import hotel.exception.booking.BookingException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Расширенный класс
 * base класс = BookingService - полная реализация интерфейса IBookingService
 */
@Component("advancedBookingService")
public class AdvancedBookingService extends BookingService {
    private static final Logger log = LoggerFactory.getLogger(AdvancedBookingService.class);

    public AdvancedBookingService() {
    }

    public boolean deleteBookingById(Integer id) {
        log.info("deleteBookingById()");
        try {
            if (bookingsRepository.findById(id).isEmpty()) {
                throw new BookingNotFoundException(id);
            }
            log.info("deleteBookingById(): booking: " + bookingsRepository.findById(id).get());
            return bookingsRepository.delete(id);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new BookingException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось удалить бронирование", e);
        }
    }

    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        log.info("updateBooking()");
        try {
            Client client = clientRepository.findById(request.getClientId()).orElse(null);
            Room room = roomRepository.findById(request.getRoomId()).orElse(null);
            if (client == null || room == null || bookingsRepository.findById(idBooking).isEmpty()) {
                throw new BookingException(ErrorCode.BOOKING);
            }
            long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
            BigDecimal roomPrice = room.getPrice().multiply(BigDecimal.valueOf(days));

            Bookings bookings = new Bookings();
            bookings.setId(idBooking);
            bookings.setClient(client.getId());
            bookings.setRoom(room.getId());
            List<Services> services = request.getServices();
            if (services == null) {
                services = new ArrayList<>();
            }
            bookings.setServices(services);
            bookings.setCheckInDate(request.getCheckInDate());
            bookings.setCheckOutDate(request.getCheckOutDate());
            BookingStatus status = request.getStatus();
            if (status == null) {
                status = BookingStatus.CANCELLED;
            }
            bookings.setStatus(status);
            bookings.setTotalPrice(roomPrice);
            bookings.setUpdatedAt(LocalDateTime.now());
            givOutCheck(idBooking);
            bookingsRepository.update(bookings);
            log.info("updateBooking(): booking: " + bookings);
            return bookingsRepository.findById(idBooking);
        } catch (SqlException | SQLException e) {
            log.error(e.getMessage());
            throw new BookingException(ErrorCode.BOOKING_DATE_CONFLICT, e.getMessage());
        }
    }
}
