package hotel.repository.room;

import hotel.annotation.Component;
import hotel.dao.RoomDAO;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JdbcRoomRepository implements RoomRepository {
    private RoomDAO roomDAO = RoomDAO.getInstance();

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
        List<Room> rooms = roomDAO.findAll();
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

    @Override
    public Optional<Room> findById(int id) throws SQLException {
        return roomDAO.findById(id);
    }

    @Override
    public List<Room> findAll() {
        return roomDAO.findAll();
    }

    @Override
    public boolean save(Room room) {
        roomDAO.save(room);
        return true;
    }

    @Override
    public boolean update(Room room) {
        roomDAO.update(room);
        return true;
    }

    @Override
    public boolean delete(int id) {
        roomDAO.delete(id);
        return true;
    }
}
