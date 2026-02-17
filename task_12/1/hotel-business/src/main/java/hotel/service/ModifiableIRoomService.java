package hotel.service;

import hotel.model.room.Room;
import hotel.model.room.RoomStatus;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface ModifiableIRoomService extends IRoomService {
    void setStatusRoom(int roomId, RoomStatus status) throws SQLException;
    void changeRoomPrice(int idRoom, BigDecimal newPrice) throws SQLException;
    void addRoom(Room room) throws SQLException;
    boolean update(Room room);
    boolean delete(int id);
}