package hotel.service;

import hotel.dto.CreateRoomRequest;
import hotel.exception.ConfigException;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class ReadRoomService implements ReadIRoomService {
    protected final RoomRepository roomRepository;
    protected final BookingsRepository bookingsRepository;
    private static final Logger log = LoggerFactory.getLogger(ReadRoomService.class);

    public ReadRoomService(RoomRepository roomRepository,
                           BookingsRepository bookingsRepository) {
        this.roomRepository = roomRepository;
        this.bookingsRepository = bookingsRepository;
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        log.info("Listing available rooms with filter: {}", filter);

        try {
            List<Room> rooms = roomRepository.listAvailableRooms(filter);
            log.info("Found {} available rooms", rooms.size());
            return rooms;
        } catch (Exception e) {
            log.error("Error while listing available rooms", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка доступных комнат", e);
        }
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        log.info("Listing available rooms for date: {} with filter: {}", date, filter);

        try {
            if (date == null) {
                throw new HotelException(ErrorCode.VALIDATION_ERROR,
                        "Дата не может быть пустой");
            }

            List<Room> allRooms = roomRepository.findAll();
            List<Bookings> allBookings = bookingsRepository.findAll();

            Set<Integer> bookedRoomIds = allBookings.stream()
                    .filter(booking -> booking.isActiveOn(date))
                    .map(booking -> booking.getRoom().getId())
                    .collect(Collectors.toSet());

            List<Room> availableRooms = allRooms.stream()
                    .filter(room -> !bookedRoomIds.contains(room.getId()))
                    .collect(Collectors.toList());

            if (filter != null) {
                availableRooms.sort(filter.getComparator());
            }

            log.info("Found {} available rooms for date {}", availableRooms.size(), date);
            return availableRooms;

        } catch (HotelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while listing available rooms by date", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске доступных комнат на дату", e);
        }
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        log.info("Sorting rooms with filter: {}", filter);

        try {
            List<Room> rooms = roomRepository.findAll();
            if (filter != null) {
                rooms.sort(filter.getComparator());
            }
            log.info("Sorted {} rooms", rooms.size());
            return rooms;
        } catch (Exception e) {
            log.error("Error while sorting rooms", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при сортировке комнат", e);
        }
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        log.info("Requesting room list and price with filter: {}", filter);

        try {
            roomRepository.requestListRoomAndPrice(filter);
            log.info("Room list request completed");
        } catch (Exception e) {
            log.error("Error while requesting room list", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при формировании отчета по комнатам", e);
        }
    }

    @Override
    public void setTotalPrice(Integer roomNumber, BigDecimal newPrice) {
        log.info("Setting price for room {} to {}", roomNumber, newPrice);

        try {
            if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new HotelException(ErrorCode.ROOM_INVALID_DATA,
                        "Цена должна быть положительным числом")
                        .addDetail("providedPrice", newPrice);
            }

            Room room = findById(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));

            BigDecimal oldPrice = room.getPrice();
            room.setPrice(newPrice);
            roomRepository.update(room);

            log.info("Price for room {} changed from {} to {}", roomNumber, oldPrice, newPrice);

        } catch (HotelException e) {
            throw e;
        } catch (SQLException e) {
            log.error("Database error while updating room price", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при обновлении цены комнаты", e);
        }
    }

    @Override
    public Optional<Room> findById(Integer id) {
        log.debug("Finding room by id: {}", id);

        try {
            if (id == null) {
                throw new HotelException(ErrorCode.VALIDATION_ERROR,
                        "ID комнаты не может быть null");
            }

            Optional<Room> room = roomRepository.findById(id);
            log.debug("Room found: {}", room.isPresent());
            return room;

        } catch (SQLException e) {
            log.error("Database error while finding room by id: {}", id, e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при поиске комнаты", e);
        }
    }

    @Override
    public List<Room> findAll() {
        log.info("Finding all rooms");

        try {
            List<Room> rooms = roomRepository.findAll();
            log.info("Found {} rooms", rooms.size());
            return rooms;
        } catch (Exception e) {
            log.error("Error while finding all rooms", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при получении списка всех комнат", e);
        }
    }

    @Override
    public Optional<Room> addRoom(CreateRoomRequest request) throws SQLException {
        throw new ConfigException();
    }

    @Override
    public void setStatusRoom(Integer roomId, RoomStatus status) throws SQLException {
        throw new ConfigException();
    }

    @Override
    public Integer save(Room room) {
        throw new ConfigException();
    }

    @Override
    public void update(Room entity) throws SQLException {
        throw new ConfigException();

    }

    @Override
    public void delete(Room room) {
        throw new ConfigException();
    }
}