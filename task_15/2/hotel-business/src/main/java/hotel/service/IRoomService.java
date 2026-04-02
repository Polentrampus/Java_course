package hotel.service;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.HotelRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRoomService extends HotelRepository<Room> {
    List<Room> listAvailableRooms(RoomFilter filter);
    List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date);
    List<Room> sortRooms(RoomFilter filter);
    void requestListRoomAndPrice(RoomFilter filter);
    void setTotalPrice(Integer roomId, BigDecimal newPrice) throws SQLException;
    void setStatusRoom(Integer roomId, RoomStatus status) throws SQLException;
    List<Room> findAll();
}
