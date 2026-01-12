package hotel.service;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface IRoomService {

    List<Room> listAvailableRooms(RoomFilter filter);
    List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date);
    List<Room> sortRooms(RoomFilter filter);
    List<Room> requestListRoom(RoomFilter filter);
    void requestListRoomAndPrice(RoomFilter filter);
}
