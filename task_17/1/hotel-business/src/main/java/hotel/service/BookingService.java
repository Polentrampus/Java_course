package hotel.service;

import hotel.dto.CreateBookingRequest;
import hotel.exception.ConfigException;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.booking.BookingCreationException;
import hotel.exception.booking.BookingDateConflictException;
import hotel.exception.booking.BookingInvalidDatesException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.exception.client.ClientNotFoundException;
import hotel.exception.dao.DAOException;
import hotel.exception.room.RoomNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class BookingService implements IBookingService {
    protected final BookingsRepository bookingsRepository;
    protected final ClientRepository clientRepository;
    protected final RoomRepository roomRepository;

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    public BookingService(BookingsRepository bookingsRepository,
                          ClientRepository clientRepository,
                          RoomRepository roomRepository) {
        this.bookingsRepository = bookingsRepository;
        this.clientRepository = clientRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Bookings> getAllBookings() {
        log.debug("Getting all bookings");
        try {
            List<Bookings> bookings = bookingsRepository.findAll();
            log.info("Found {} bookings", bookings.size());
            return bookings;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting all bookings", e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Ошибка при получении списка бронирований", e);
        }
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) {
        log.info("Getting booking by id: {}", id);

        if (id == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID бронирования не может быть null");
        }

        try {
            Optional<Bookings> booking = bookingsRepository.findById(id);
            if (booking.isPresent()) {
                log.info("Booking found with id: {}", id);
            } else {
                log.debug("Booking not found with id: {}", id);
            }
            return booking;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while getting booking by id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске бронирования", e);
        }
    }

    @Override
    public List<Bookings> findActiveBookings(LocalDate date) {
        log.info("Finding active bookings for date: {}", date);

        if (date == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Дата не может быть null");
        }

        try {
            List<Bookings> bookings = bookingsRepository.findActiveBookings(date);
            log.info("Found {} active bookings for date {}", bookings.size(), date);
            return bookings;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding active bookings for date: {}", date, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске активных бронирований", e);
        }
    }

    @Override
    public List<Bookings> findByRoomId(Integer roomId) {
        log.info("Finding bookings for room: {}", roomId);

        if (roomId == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID комнаты не может быть null");
        }

        try {
            List<Bookings> bookings = bookingsRepository.findByRoomId(roomId);
            log.info("Found {} bookings for room {}", bookings.size(), roomId);
            return bookings;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding bookings for room: {}", roomId, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске бронирований по комнате", e);
        }
    }

    @Override
    public List<Bookings> findByClientId(Integer clientId) {
        log.info("Finding bookings for client: {}", clientId);

        if (clientId == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID клиента не может быть null");
        }

        try {
            List<Bookings> bookings = bookingsRepository.findByClientId(clientId);
            log.info("Found {} bookings for client {}", bookings.size(), clientId);
            return bookings;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding bookings for client: {}", clientId, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске бронирований по клиенту", e);
        }
    }

    @Override
    public List<Bookings> findActiveByRoomId(Integer roomId, LocalDate date) {
        log.info("Finding active bookings for room: {}, date: {}", roomId, date);

        if (roomId == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID комнаты не может быть null");
        }
        if (date == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Дата не может быть null");
        }

        try {
            List<Bookings> bookings = bookingsRepository.findActiveByRoomId(roomId, date);
            log.info("Found {} active bookings for room {} on date {}", bookings.size(), roomId, date);
            return bookings;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding active bookings for room: {}, date: {}", roomId, date, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске активных бронирований по комнате", e);
        }
    }

    @Override
    public List<Bookings> findActiveByClientId(Integer clientId) {
        log.info("Finding active bookings for client: {}", clientId);

        if (clientId == null) {
            log.warn("Client ID is null, returning empty list");
            return Collections.emptyList();
        }

        try {
            List<Bookings> allActiveBookings = bookingsRepository.findActiveBookings(LocalDate.now());

            List<Bookings> clientBookings = allActiveBookings.stream()
                    .filter(booking -> booking != null)
                    .filter(booking -> {
                        Client client = booking.getClient();
                        return client != null && clientId.equals(client.getId());
                    })
                    .collect(Collectors.toList());

            log.info("Found {} active bookings for client {}", clientBookings.size(), clientId);
            return clientBookings;
        } catch (DAOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while finding active bookings for client: {}", clientId, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске активных бронирований по клиенту", e);
        }
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) {
        log.info("Creating booking for client: {}, room: {}",
                request.getClientId(), request.getRoomId());

        validateCreateBookingRequest(request);

        try {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ClientNotFoundException(request.getClientId()));

            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));

            checkRoomAvailability(room.getId(), request.getCheckInDate(), request.getCheckOutDate());

            BigDecimal totalPrice = calculateTotalPrice(
                    room,
                    request.getCheckInDate(),
                    request.getCheckOutDate(),
                    request.getServices()
            );

            Bookings booking = new Bookings();
            booking.setClient(client);
            booking.setRoom(room);
            booking.setServices(request.getServices());
            booking.setCheckInDate(request.getCheckInDate());
            booking.setCheckOutDate(request.getCheckOutDate());
            booking.setStatus(request.getStatus() != null ? request.getStatus() : BookingStatus.CONFIRMED);
            booking.setTotalPrice(totalPrice);
            booking.setCreatedAt(LocalDateTime.now());

            Integer savedId = bookingsRepository.save(booking);
            log.info("Booking created with ID: {}", savedId);

            return bookingsRepository.findById(savedId);

        } catch (ClientNotFoundException | RoomNotFoundException | BookingDateConflictException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while creating booking", e);
            throw new BookingCreationException("Ошибка при сохранении бронирования в БД", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating booking", e);
            throw new BookingCreationException("Непредвиденная ошибка при создании бронирования", e);
        }
    }

    @Override
    public BigDecimal givOutCheck(Integer idBooking) {
        log.info("Calculating check for booking: {}", idBooking);

        if (idBooking == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID бронирования не может быть null");
        }

        try {
            Bookings booking = bookingsRepository.findById(idBooking)
                    .orElseThrow(() -> new BookingNotFoundException(idBooking));

            if (booking.getRoom() == null) {
                throw new HotelException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                        "Комната не найдена в бронировании");
            }

            long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
            BigDecimal roomPrice = booking.getRoom().getPrice().multiply(BigDecimal.valueOf(days));

            BigDecimal servicesPrice = calculateServicesPrice(booking.getServices());
            BigDecimal totalPrice = roomPrice.add(servicesPrice);

            booking.setTotalPrice(totalPrice);
            bookingsRepository.update(booking);

            log.info("Check calculated for booking {}: {}", idBooking, totalPrice);
            return totalPrice;

        } catch (BookingNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while calculating check for booking: {}", idBooking, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при расчете стоимости бронирования", e);
        } catch (Exception e) {
            log.error("Unexpected error while calculating check for booking: {}", idBooking, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при расчете стоимости", e);
        }
    }

    @Override
    public Optional<Bookings> addServiceToBooking(Integer bookingId, List<Integer> serviceIds) {
        log.info("Adding services to booking: {}, services: {}", bookingId, serviceIds);

        if (bookingId == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID бронирования не может быть null");
        }
        if (serviceIds == null || serviceIds.isEmpty()) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Список услуг не может быть пустым");
        }

        try {
            Bookings booking = bookingsRepository.findById(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException(bookingId));

            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new HotelException(ErrorCode.BOOKING,
                        "Нельзя добавить услуги к отмененному бронированию");
            }

            if (booking.getCheckOutDate().isBefore(LocalDate.now())) {
                throw new HotelException(ErrorCode.BOOKING,
                        "Нельзя добавить услуги к завершенному бронированию");
            }

            Optional<Bookings> updatedBooking = bookingsRepository.addBookingServices(bookingId, serviceIds);
            log.info("Services added to booking: {}", bookingId);
            return updatedBooking;

        } catch (BookingNotFoundException e) {
            throw e;
        } catch (DAOException e) {
            log.error("Database error while adding services to booking: {}", bookingId, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при добавлении услуг к бронированию", e);
        } catch (Exception e) {
            log.error("Unexpected error while adding services to booking: {}", bookingId, e);
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Непредвиденная ошибка при добавлении услуг", e);
        }
    }

    @Override
    public void deleteBookingById(Integer id) throws SQLException {
        throw new ConfigException();
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) throws SQLException {
        throw new ConfigException();
    }

    private void validateCreateBookingRequest(CreateBookingRequest request) {
        if (request == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "Запрос на создание бронирования не может быть null");
        }
        if (request.getClientId() == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID клиента обязателен");
        }
        if (request.getRoomId() == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR, "ID комнаты обязателен");
        }
        if (request.getCheckInDate() == null) {
            throw new BookingInvalidDatesException("Дата заезда обязательна");
        }
        if (request.getCheckOutDate() == null) {
            throw new BookingInvalidDatesException("Дата выезда обязательна");
        }
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BookingInvalidDatesException("Дата заезда не может быть в прошлом");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new BookingInvalidDatesException("Дата выезда должна быть позже даты заезда");
        }
    }

    void checkRoomAvailability(Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        List<Bookings> existingBookings = bookingsRepository.findActiveByRoomId(roomId, LocalDate.now());

        boolean isAvailable = existingBookings.stream()
                .noneMatch(booking -> datesOverlap(
                        checkIn, checkOut,
                        booking.getCheckInDate(), booking.getCheckOutDate()
                ));

        if (!isAvailable) {
            throw new BookingDateConflictException(roomId, checkIn, checkOut);
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    BigDecimal calculateTotalPrice(Room room, LocalDate checkIn, LocalDate checkOut, List<Services> services) {
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal roomPrice = room.getPrice().multiply(BigDecimal.valueOf(days));
        BigDecimal servicesPrice = calculateServicesPrice(services);
        return roomPrice.add(servicesPrice);
    }

    private BigDecimal calculateServicesPrice(List<Services> services) {
        if (services == null || services.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return services.stream()
                .map(Services::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}