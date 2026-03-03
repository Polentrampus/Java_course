package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component("readRoomService")
public class ReadRoomService implements ReadIRoomService {
    @Inject
    public RoomRepository roomRepository;

    @Inject
    public BookingsRepository bookingsRepository;

    @Inject
    public TransactionManager transactionManager;

    private static final Logger log = LoggerFactory.getLogger(ReadRoomService.class);

    public ReadRoomService() {
        log.debug(">>> ReadRoomService CREATED with hash: {}", this.hashCode());
        log.debug(">>> Stack trace:", new Exception("Stack trace"));
    }

    public void checkInjection() {
        log.info("Checking injection in ReadRoomService:");
        log.info("  transactionManager = {}", transactionManager);
        log.info("  roomRepository = {}", roomRepository);
        log.info("  bookingsRepository = {}", bookingsRepository);
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        log.info("listAvailableRooms() with filter: {}", filter);

        return transactionManager.executeInTransaction(() -> {
            List<Room> rooms = roomRepository.listAvailableRooms(filter);
            log.info("listAvailableRooms(): found {} available rooms", rooms.size());
            return rooms;
        });
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        log.info("listAvailableRoomsByDate() for date: {}", date);

        return transactionManager.executeInTransaction(() -> {
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
        });
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        log.info("sortRooms() with filter: {}", filter);

        return transactionManager.executeInTransaction(() -> {
            List<Room> rooms = roomRepository.sortRooms(filter);
            log.info("sortRooms(): sorted {} rooms", rooms.size());
            return rooms;
        });
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        log.info("requestListRoomAndPrice() with filter: {}", filter);

        transactionManager.executeInTransaction(() -> {
            roomRepository.requestListRoomAndPrice(filter);
            log.info("requestListRoomAndPrice(): completed");
            return null;
        });
    }

    @Override
    public void setTotalPrice(Integer roomNumber, BigDecimal newPrice) {
        log.info("setTotalPrice() for room: {}, new price: {}", roomNumber, newPrice);

        transactionManager.executeInTransaction(() -> {
            Room room = roomRepository.findById(roomNumber)
                    .orElseThrow(() -> new HotelException(ErrorCode.ROOM_NOT_FOUND,
                            "Комната не найдена с номером: " + roomNumber));

            room.setPrice(newPrice);
            roomRepository.update(room);

            log.info("setTotalPrice(): set price for room {} to {}", roomNumber, newPrice);
            return null;
        });
    }

    @Override
    public Optional<Room> findById(Integer id) {
        log.info("findById() for id: {}", id);

        return transactionManager.executeInTransaction(() -> {
            Optional<Room> room = roomRepository.findById(id);
            log.info("findById(): room found: {}", room.isPresent());
            return room;
        });
    }

    @Override
    public List<Room> findAll() {
        log.info("findAll()");
        checkInjection();
        return transactionManager.executeInTransaction(() -> {
            List<Room> rooms = roomRepository.findAll();
            log.info("findAll(): found {} rooms", rooms.size());
            return rooms;
        });
    }

    @Override
    public void setStatusRoom(Integer roomId, RoomStatus status) {
        throw new ConfigException();
    }

    @Override
    public Integer save(Room room) {
        throw new ConfigException();
    }

    @Override
    public void update(Room entity) {
        throw new ConfigException();

    }

    @Override
    public void delete(Room room) {
        throw new ConfigException();
    }
}