package hotel.service;

import hotel.annotation.Component;
import hotel.dto.CreateBookingRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.SqlException;
import hotel.exception.booking.BookingException;
import hotel.exception.booking.BookingNotFoundException;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.client.ClientRepository;
import hotel.repository.room.RoomRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookingService implements IBookingService{
    private BookingsRepository bookingsRepository;
    private ClientRepository clientRepository;
    private RoomRepository roomRepository;

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
        try{
            return bookingsRepository.findAll();
        }catch (Exception e) {
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    @Override
    public Optional<Bookings> getBookingById(Integer id) throws SQLException {
        return bookingsRepository.findById(id);
    }

    public List<Bookings> findActiveBookings(LocalDate date) {
        try{
            return bookingsRepository.findActiveBookings(date);
        }catch (Exception e) {
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    public List<Bookings> findByRoomId(int roomId) {
        try{
            return bookingsRepository.findByRoomId(roomId);
        }catch (Exception e) {
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    public List<Bookings> findByClientId(int clientId) {
        try{
            return bookingsRepository.findByClientId(clientId);
        }catch (Exception e) {
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось получить список бронирований", e);
        }
    }

    @Override
    public Optional<Bookings> findActiveByRoomId(int idRoom, LocalDate date) {
        return bookingsRepository.findActiveByRoomId(idRoom, date);
    }

    @Override
    public Optional<Bookings> findActiveByClientId(int idClient) {
        List<Bookings> bookings = bookingsRepository.findActiveBookings(LocalDate.now());
        for (Bookings booking : bookings) {
            if(booking.getClient().equals(idClient) && booking.getClient() != null )
                return Optional.of(booking);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Bookings> createBooking(CreateBookingRequest request) throws SQLException {
        try{
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
                return createdBooking;
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка базы данных при создании бронирования", e);
        } catch (Exception e) {
            throw new HotelException(ErrorCode.UNEXPECTED_ERROR,
                    "Не удалось создать бронь", e);
        }
    }

    @Override
    public boolean deleteBookingById(Integer id) {
        try {
            if (bookingsRepository.findById(id).isEmpty()) {
                throw new BookingNotFoundException(id);
            }

            return bookingsRepository.delete(id);

        } catch (SQLException e) {
            throw new BookingException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Не удалось удалить бронирование", e);
        }
    }

    @Override
    public Optional<Bookings> updateBooking(CreateBookingRequest request, Integer idBooking) throws SQLException {
        try{
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
            return bookingsRepository.findById(idBooking);
        }catch (SqlException e){
            throw new BookingException(ErrorCode.BOOKING_DATE_CONFLICT,e.getMessage());
        }
    }

    @Override
    public BigDecimal givOutCheck(int idBooking) throws SQLException {
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
            return bookingsRepository.findById(idBooking).get().getTotalPrice();
        }catch (SqlException e){
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,e.getMessage());
        }
    }
}