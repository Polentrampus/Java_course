package hotel.service;

import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;

public interface ModifiableIRoomService extends IRoomService {
    void setStatusRoom(int roomId, RoomStatus status);
    void changeRoomPrice(int idRoom, int newPrice);
    void addRoom(RoomCategory category, RoomStatus status, RoomType type, int capacity, int roomNumber, int price);

}
