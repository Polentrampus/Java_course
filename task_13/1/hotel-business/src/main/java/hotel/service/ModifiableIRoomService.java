package hotel.service;

import hotel.model.room.Room;
import hotel.model.room.RoomStatus;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface ModifiableIRoomService extends IRoomService {
    void setStatusRoom(Integer roomId, RoomStatus status) throws SQLException;
    void changeRoomPrice(Integer idRoom, BigDecimal newPrice) throws SQLException;
    void addRoom(Room room) throws SQLException;
}