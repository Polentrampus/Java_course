package hotel.service;

import hotel.config.HotelConfiguration;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.SqlException;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public abstract class RoomService implements IRoomService {
    protected RoomRepository roomRepository;
    protected BookingsRepository bookingsRepository;
    protected HotelConfiguration config;
    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    @Override
    public void setHotelRepository(RoomRepository roomRepository, BookingsRepository bookingsRepository) {
        this.roomRepository = roomRepository;
        this.bookingsRepository = bookingsRepository;
    }

    public RoomService() {
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        log.info("listAvailableRooms()");
        try {
            List<Room> rooms = roomRepository.listAvailableRooms(filter);
            log.info("listAvailableRooms(): available rooms size: " + rooms.size());
            return rooms;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.ROOM_NOT_AVAILABLE, e.getMessage());
        }
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        log.info("listAvailableRoomsByDate()");
        try {
            List<Room> rooms = roomRepository.findAll();
            for (Bookings bookings : bookingsRepository.findAll()) {
                if (bookings.getCheckInDate().isBefore(date) && bookings.getCheckOutDate().isAfter(date)) {
                    rooms.remove(bookings.getRoom());
                }
            }
            log.info("listAvailableRoomsByDate(): available rooms size: " + rooms.size());
            return rooms;
        } catch (SqlException e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR, e.getMessage());
        }
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        log.info("sortRooms()");
        try {
            List<Room> rooms = roomRepository.sortRooms(filter);
            log.info("sortRooms(): sorted rooms size: " + rooms.size());
            return rooms;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.ROOM_NOT_AVAILABLE, e.getMessage());
        }
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        log.info("requestListRoomAndPrice()");
        roomRepository.requestListRoomAndPrice(filter);
        log.info("requestListRoomAndPrice(): completed");
    }

    @Override
    public void setTotalPrice(int roomNumber, BigDecimal newPrice) throws SQLException {
        log.info("setTotalPrice()");
        Room room = roomRepository.findById(roomNumber).get();
        room.setPrice(newPrice);
        log.info("setTotalPrice(): set price for room " + roomNumber + " to " + newPrice);
    }

    @Override
    public Optional<Room> findById(int id) throws SQLException {
        log.info("findById()");
        Optional<Room> room = roomRepository.findById(id);
        log.info("findById(): room: " + room);
        return room;
    }

    @Override
    public List<Room> findAll() {
        log.info("findAll()");
        try {
            List<Room> rooms = roomRepository.findAll();
            log.info("findAll(): rooms size: " + rooms.size());
            return rooms;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HotelException(ErrorCode.ROOM_NOT_AVAILABLE, e.getMessage());
        }
    }
}
