package hotel.repository.room;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.repository.HotelRepository;

import java.util.List;

public interface RoomRepository extends HotelRepository<Room> {
    List<Room> listAvailableRooms(RoomFilter filter);
    List<Room> sortRooms(RoomFilter filter);
    void requestListRoomAndPrice(RoomFilter filter);
}
