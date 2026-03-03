package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("bookingService")
public class BookingService implements IBookingService {
    @Inject
    public BookingsRepository bookingsRepository;
    @Inject
    public ClientRepository clientRepository;
    @Inject
    public RoomRepository roomRepository;
    @Inject
    public TransactionManager transactionManager;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    public BookingService() {}

    public void checkInitialization() {
        log.debug("Checking initialization in BookingService:");
        log.debug("  transactionManager = {}", transactionManager);
        log.debug("  bookingsRepository = {}", bookingsRepository);
        log.debug("  clientRepository = {}", clientRepository);
        log.debug("  roomRepository = {}", roomRepository);
    }

    @Override
    public List<Bookings> getAllBookings() {
        log.debug("getAllBookings()");
        checkInitialization();
        return transactionManager.executeInTransaction(() -> {
            List<Bookings> bookings = bookingsRepository.findAll();
            log.info("bookings found");
            return bookings;
        });
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) {
        log.info("getBookingById() for id: {}", id);
        return transactionManager.executeInTransaction(() -> {
            Optional<Bookings> bookings = bookingsRepository.findById(id);
            log.info("getBookingById(): booking found: {}", bookings);
            return bookings;
        });
    }

    public List<Bookings> findActiveBookings(LocalDate date) {
        log.info("findActiveBookings() for date: {}", date);
        return transactionManager.executeInTransaction(() -> {
            List<Bookings> bookings = bookingsRepository.findActiveBookings(date);
            log.info("findActiveBookings(): found {} bookings", bookings.size());
            return bookings;
        });
    }

    @Override
    public List<Bookings> findByRoomId(Integer roomId) {
        log.info("findByRoomId() for room: {}", roomId);
        return transactionManager.executeInTransaction(() -> {
            List<Bookings> bookings = bookingsRepository.findByRoomId(roomId);
            log.info("findByRoomId(): found {} bookings", bookings.size());
            return bookings;
        });
    }

    @Override
    public List<Bookings> findByClientId(Integer clientId) {
        log.info("findByClientId() for client: {}", clientId);
        return transactionManager.executeInTransaction(() -> {
            List<Bookings> bookings = bookingsRepository.findByClientId(clientId);
            log.info("findByClientId(): found {} bookings", bookings.size());
            return bookings;
        });
    }

    @Override
    public List<Bookings> findActiveByRoomId(Integer idRoom, LocalDate date) {
        log.info("findActiveByRoomId() for room: {}, date: {}", idRoom, date);
        return transactionManager.executeInTransaction(() -> {
            List<Bookings> bookings = bookingsRepository.findActiveByRoomId(idRoom, date);
            log.info("findActiveByRoomId(): found {} bookings", bookings.size());
            return bookings;
        });
    }

    @Override
    public List<Bookings> findActiveByClientId(Integer idClient) {
        log.info("findActiveByClientId() for client: {}", idClient);

        if (idClient == null) {
            log.warn("Client ID is null, returning empty list");
            return Collections.emptyList();
        }

        return transactionManager.executeInTransaction(() -> {
            List<Bookings> allActiveBookings = bookingsRepository.findActiveBookings(LocalDate.now());

            List<Bookings> clientBookings = allActiveBookings.stream()
                    .filter(booking -> booking != null)
                    .filter(booking -> {
                        Client client = booking.getClient();
                        return client != null && idClient.equals(client.getId());
                    })
                    .collect(Collectors.toList());

            log.info("findActiveByClientId(): found {} bookings for client {}",
                    clientBookings.size(), idClient);
            return clientBookings;
        });
    }

    @Override
    public void deleteBookingById(Integer id) {
        throw new ConfigException();
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) {
        throw new ConfigException();
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) throws SQLException {
        log.info("createBooking()");
        return transactionManager.executeInTransaction(() -> {
            log.debug("Transaction started");

            Optional<Client> client = clientRepository.findById(request.getClientId());
            Optional<Room> room = roomRepository.findById(request.getRoomId());

            if (client.isEmpty() || room.isEmpty()) {
                throw new BookingException(ErrorCode.BOOKING, "Невозможно создать бронь, проверьте заказ!");
            }

            List<Bookings> existingBookings = bookingsRepository.findActiveByRoomId(
                    request.getRoomId(),
                    LocalDate.now()
            );

            boolean isRoomAvailable = existingBookings.stream()
                    .noneMatch(booking ->
                            !(request.getCheckOutDate().isBefore(booking.getCheckInDate()) ||
                                    request.getCheckInDate().isAfter(booking.getCheckOutDate()))
                    );

            if (!isRoomAvailable) {
                throw new BookingException(
                        ErrorCode.BOOKING,
                        "Комната недоступна на выбранные даты"
                );
            }

            long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
            if (days <= 0) {
                throw new BookingException(
                        ErrorCode.BOOKING,
                        "Дата выезда должна быть позже даты заезда"
                );
            }

            BigDecimal roomPrice = room.get().getPrice().multiply(BigDecimal.valueOf(days));

            BigDecimal servicesPrice = BigDecimal.ZERO;
            List<Services> services = request.getServices();
            if (services != null && !services.isEmpty()) {
                for (Services service : services) {
                    servicesPrice = servicesPrice.add(service.getPrice());
                }
            }

            BigDecimal totalPrice = roomPrice.add(servicesPrice);

            Bookings booking = new Bookings();
            booking.setClient(client.get());
            booking.setRoom(room.get());
            booking.setServices(services);
            booking.setCheckInDate(request.getCheckInDate());
            booking.setCheckOutDate(request.getCheckOutDate());

            BookingStatus status = request.getStatus();
            booking.setStatus(status != null ? status : BookingStatus.CONFIRMED);
            booking.setTotalPrice(totalPrice);
            booking.setCreatedAt(LocalDateTime.now());

            Integer savedId = bookingsRepository.save(booking);
            log.info("Booking created with ID: {}", savedId);

            Optional<Bookings> createdBooking = bookingsRepository.findById(savedId);

            if (createdBooking.isPresent()) {
                BigDecimal check = givOutCheck(createdBooking.get().getId());
                log.info("Check calculated for booking {}: {}", savedId, check);
            }

            log.info("createBooking(): booking created successfully");
            return createdBooking;
        });
    }

    @Override
    public BigDecimal givOutCheck(Integer idBooking) throws SQLException {
        log.info("givOutCheck()");
        try {
            Optional<Bookings> bookings = bookingsRepository.findById(idBooking);
            if (bookings == null) {
                throw new SqlException("Бронирование не найдено");
            }
            if (bookings.get().getRoom() == null) {
                throw new SqlException("Комната не найдена в бронировании");
            }
            long days = ChronoUnit.DAYS.between(
                    bookings.get().getCheckInDate(),
                    bookings.get().getCheckOutDate()
            );
            BigDecimal sum = BigDecimal.valueOf(days);
            sum = sum.multiply(roomRepository.findById(bookings.get().getRoom().getId()).get().getPrice());
            BigDecimal servicesPrice = BigDecimal.ZERO;
            List<Services> services = bookings.get().getServices();
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