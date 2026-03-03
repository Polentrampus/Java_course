package hotel.service;

import hotel.dto.CreateBookingRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.exception.client.ClientNotFoundException;
import hotel.exception.dao.DAOException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.users.client.Client;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
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

    @Override
    public void deleteBookingById(Integer id) {
        log.info("Deleting booking with id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID бронирования не может быть null");
        }

        try {
            Bookings booking = bookingsRepository.findById(id)
                    .orElseThrow(() -> new BookingNotFoundException(id));

            if (booking.getCheckInDate().isBefore(LocalDate.now()) &&
                    booking.getCheckOutDate().isAfter(LocalDate.now())) {
                log.warn("Attempting to delete active booking: {}", id);
            }

            bookingsRepository.delete(booking);
            log.info("Booking deleted successfully with id: {}", id);

        } catch (BookingNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while deleting booking: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при удалении бронирования", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting booking: {}", id, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при удалении бронирования", e);
        }
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        log.info("Updating booking with id: {}", idBooking);

        if (idBooking == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID бронирования не может быть null");
        }
        if (request == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Запрос на обновление не может быть null");
        }

        try {
            Bookings existingBooking = bookingsRepository.findById(idBooking)
                    .orElseThrow(() -> new BookingNotFoundException(idBooking));

            if (existingBooking.getCheckInDate().isBefore(LocalDate.now())) {
                throw new HotelException(ErrorCode.BOOKING,
                        "Нельзя обновить бронирование после даты заезда");
            }

            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));

            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ClientNotFoundException(request.getClientId()));

            if (!request.getCheckInDate().equals(existingBooking.getCheckInDate()) ||
                    !request.getCheckOutDate().equals(existingBooking.getCheckOutDate())) {
                checkRoomAvailability(room.getId(), request.getCheckInDate(), request.getCheckOutDate());
            }

            existingBooking.setServices(request.getServices());
            existingBooking.setRoom(room);
            existingBooking.setClient(client);
            existingBooking.setCheckInDate(request.getCheckInDate());
            existingBooking.setCheckOutDate(request.getCheckOutDate());

            BigDecimal totalPrice = calculateTotalPrice(
                    room,
                    request.getCheckInDate(),
                    request.getCheckOutDate(),
                    request.getServices()
            );
            existingBooking.setTotalPrice(totalPrice);

            bookingsRepository.update(existingBooking);
            log.info("Booking updated successfully with id: {}", idBooking);

            return Optional.of(existingBooking);

        } catch (BookingNotFoundException | RoomNotFoundException | ClientNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while updating booking: {}", idBooking, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при обновлении бронирования", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating booking: {}", idBooking, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при обновлении бронирования", e);
        }
    }
}
