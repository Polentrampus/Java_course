package hotel.repository.room;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HibernateRoomRepository extends BaseRepository<Room, Integer> implements RoomRepository {
    public HibernateRoomRepository() {
        setEntityClass(Room.class);
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        List<Room> rooms = sortRooms(filter);
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        List<Room> rooms = findAll();
        rooms.sort(filter.getComparator());
        return rooms;
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        System.out.println("Номер комнаты/цена");
        for (Room room : sortRooms(filter)) {
            System.out.println(room.getNumber() + " " + room.getPrice());
        }
    }
}
