package hotel.service;

import hotel.annotation.Component;
import hotel.dto.CreateBookingRequest;
import hotel.exception.ConfigException;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.SqlException;
import hotel.exception.booking.BookingException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component("bookingService")
public class BookingService implements IBookingService {
    protected BookingsRepository bookingsRepository;
    protected ClientRepository clientRepository;
    protected RoomRepository roomRepository;
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    @Override
    public void setHotelRepository(BookingsRepository bookingsRepository,
                                   ClientRepository clientRepository,
                                   RoomRepository roomRepository) {
        this.bookingsRepository = bookingsRepository;
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
    }

    public BookingService() {
    }

    @Override
    public List<Bookings> getAllBookings() {
        log.info("getAllBookings()");
        try {
            List<Bookings> bookings = bookingsRepository.findAll();
            log.info("getAllBookings(): bookings size: " + bookings.size());
            return bookings;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) throws SQLException {
        log.info("getBookingById()");
        Optional<Bookings> bookings = bookingsRepository.findById(id);
        log.info("getBookingById(): booking: " + bookings);
        return bookings;
    }

    public List<Bookings> findActiveBookings(LocalDate date) {
        log.info("findActiveBookings()");
        try {
            List<Bookings> bookings = bookingsRepository.findActiveBookings(date);
            log.info("findActiveBookings(): bookings size: " + bookings.size());
            return bookings;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    public List<Bookings> findByRoomId(int roomId) {
        log.info("findByRoomId()");
        try {
            List<Bookings> bookings = bookingsRepository.findByRoomId(roomId);
            log.info("findByRoomId(): bookings size: " + bookings.size());
            return bookings;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    public List<Bookings> findByClientId(int clientId) {
        log.info("findByClientId()");
        try {
            List<Bookings> bookings = bookingsRepository.findByClientId(clientId);
            log.info("findByClientId(): bookings size: " + bookings.size());
            return bookings;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    @Override
    public Optional<Bookings> findActiveByRoomId(int idRoom, LocalDate date) {
        log.info("findActiveByRoomId()");
        Optional<Bookings> bookings = bookingsRepository.findActiveByRoomId(idRoom, date);
        log.info("findActiveByRoomId(): booking: " + bookings);
        return bookings;
    }

    @Override
    public Optional<Bookings> findActiveByClientId(int idClient) {
        log.info("findActiveByClientId()");
        List<Bookings> bookings = bookingsRepository.findActiveBookings(LocalDate.now());
        for (Bookings booking : bookings) {
            if (booking.getClient().equals(idClient) && booking.getClient() != null) {
                log.info("findActiveByClientId(): booking: " + booking);
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteBookingById(Integer id) {
        throw new ConfigException();
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        throw new ConfigException();
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) throws SQLException {
        log.info("createBooking()");
        try {
            Client client = clientRepository.findById(request.getClientId()).orElse(null);
            Room room = roomRepository.findById(request.getRoomId()).orElse(null);

            if (client == null || room == null) {
                throw new BookingException(ErrorCode.BOOKING, "Невозможно создать бронь, проверьте заказ!");
            }
            long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
            BigDecimal roomPrice = room.getPrice().multiply(BigDecimal.valueOf(days));

            BigDecimal servicesPrice = BigDecimal.ZERO;
            List<Services> services = request.getServices();
            if (services != null && !services.isEmpty()) {
                for (Services service : services) {
                    servicesPrice = servicesPrice.add(service.getPrice());
                }
            }
            BigDecimal totalPrice = roomPrice.add(servicesPrice);

            Bookings bookings = new Bookings();
            bookings.setClient(client.getId());
            bookings.setRoom(room.getId());
            bookings.setServices(services);
            bookings.setCheckInDate(request.getCheckInDate());
            bookings.setCheckOutDate(request.getCheckOutDate());
            BookingStatus status = request.getStatus();

            if (status == null) {
                status = BookingStatus.CANCELLED;
            }

            bookings.setStatus(status);
            bookings.setTotalPrice(totalPrice);
            bookings.setCreatedAt(LocalDateTime.now());
            boolean saved = bookingsRepository.save(bookings);
            if (!saved) {
                throw new SqlException("Не удалось сохранить бронирование");
            }
            Optional<Bookings> createdBooking = bookingsRepository.findAll()
                    .stream()
                    .filter(b -> b.getCheckInDate().equals(request.getCheckInDate())
                            && b.getCheckOutDate().equals(request.getCheckOutDate())
                            && b.getClient().equals(client.getId()))
                    .findFirst();

            if (createdBooking.isPresent()) {
                givOutCheck(createdBooking.get().getId());
                log.info("createBooking(): booking: " + createdBooking);
                return createdBooking;
            }
            log.info("createBooking(): booking: " + bookings);
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Failed to create booking", e,
                    "clientId", request.getClientId(),
                    "roomId", request.getRoomId());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка базы данных при создании бронирования", e);
        } catch (Exception e) {
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Не удалось создать бронь", e);
        }
    }

    @Override
    public BigDecimal givOutCheck(int idBooking) throws SQLException {
        log.info("givOutCheck()");
        try {
            Bookings bookings = bookingsRepository.findById(idBooking).orElse(null);
            if (bookings == null) {
                throw new SqlException("Бронирование не найдено");
            }
            if (bookings.getRoom() == null) {
                throw new SqlException("Комната не найдена в бронировании");
            }
            long days = ChronoUnit.DAYS.between(
                    bookings.getCheckInDate(),
                    bookings.getCheckOutDate()
            );
            BigDecimal sum = BigDecimal.valueOf(days);
            sum = sum.multiply(roomRepository.findById(bookings.getRoom()).get().getPrice());
            BigDecimal servicesPrice = BigDecimal.ZERO;
            List<Services> services = bookings.getServices();
            if (services != null) {
                for (Services service : services) {
                    servicesPrice = servicesPrice.add(service.getPrice());
                }
            }
            sum = sum.add(servicesPrice);
            bookingsRepository.findById(idBooking).get().setTotalPrice(sum);
            BigDecimal res = bookingsRepository.findById(idBooking).get().getTotalPrice();
            log.info("givOutCheck(): res: " + res);
            return res;
        } catch (SqlException e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR, e.getMessage());
        }
    }
}