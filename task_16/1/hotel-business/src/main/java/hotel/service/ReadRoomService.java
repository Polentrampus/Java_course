package hotel.service;

import hotel.dto.CreateRoomRequest;
import hotel.exception.ConfigException;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
    public RoomRepository roomRepository;
    public BookingsRepository bookingsRepository;
    private static final Logger log = LoggerFactory.getLogger(ReadRoomService.class);

    public ReadRoomService(RoomRepository roomRepository,
                           BookingsRepository bookingsRepository) {
        this.roomRepository = roomRepository;
        this.bookingsRepository = bookingsRepository;
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        log.info("listAvailableRooms() with filter: {}", filter);

        List<Room> rooms = roomRepository.listAvailableRooms(filter);
        log.info("listAvailableRooms(): found {} available rooms", rooms.size());
        return rooms;
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        log.info("listAvailableRoomsByDate() for date: {}", date);
        List<Room> allRooms = roomRepository.findAll();
        List<Bookings> allBookings = bookingsRepository.findAll();

        Set<Integer> bookedRoomIds = allBookings.stream()
                .filter(booking -> booking.isActiveOn(date))
                .map(booking -> booking.getRoom().getId())
                .collect(Collectors.toSet());

        List<Room> availableRooms = allRooms.stream()
                .filter(room -> !bookedRoomIds.contains(room.getId()))
                .collect(Collectors.toList());

        log.info("listAvailableRoomsByDate(): found {} available rooms for date {}",
                availableRooms.size(), date);
        return availableRooms;
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        log.info("sortRooms() with filter: {}", filter);
        List<Room> rooms = roomRepository.findAll();
        if (filter != null) {
            rooms.sort(filter.getComparator());
        }
        log.info("sortRooms(): sorted {} rooms", rooms.size());
        return rooms;
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        log.info("requestListRoomAndPrice() with filter: {}", filter);
        roomRepository.requestListRoomAndPrice(filter);
        log.info("requestListRoomAndPrice(): completed");
    }

    @Override
    public void setTotalPrice(Integer roomNumber, BigDecimal newPrice) throws SQLException {
        log.info("setTotalPrice() for room: {}, new price: {}", roomNumber, newPrice);
        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new HotelException(ErrorCode.ROOM_NOT_FOUND,
                        "Комната не найдена с номером: " + roomNumber));

        room.setPrice(newPrice);
        roomRepository.update(room);

        log.info("setTotalPrice(): set price for room {} to {}", roomNumber, newPrice);
    }

    @Override
    public Optional<Room> findById(Integer id) throws SQLException {
        log.info("findById() for id: {}", id);
        Optional<Room> room = roomRepository.findById(id);
        log.info("findById(): room found: {}", room.isPresent());
        return room;
    }

    @Override
    public List<Room> findAll() {
        log.info("findAll()");
        List<Room> rooms = roomRepository.findAll();
        log.info("findAll(): found {} rooms", rooms.size());
        return rooms;
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