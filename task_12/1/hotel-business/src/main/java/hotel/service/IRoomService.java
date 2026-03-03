package hotel.service;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomService {
    List<Room> listAvailableRooms(RoomFilter filter);
    List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date);
    List<Room> sortRooms(RoomFilter filter);
    void requestListRoomAndPrice(RoomFilter filter);
    void setHotelRepository(RoomRepository roomRepo, BookingsRepository bookingsRepo);
    void setTotalPrice(int roomId, BigDecimal newPrice) throws SQLException;
    Optional<Room> findById(int id) throws SQLException;
    List<Room> findAll();
    boolean save(Room room) throws SQLException;
}
