package hotel.repository.room;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.util.JsonDataManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JsonRoomRepository implements RoomRepository {
    private final JsonDataManager dataManager = JsonDataManager.getInstance();

    public JsonRoomRepository() {
    }

    @Override
    public Optional<Room> findById(int id) throws SQLException {
        return Optional.of(dataManager.getRooms().get(id));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(dataManager.getRooms().values());
    }

    @Override
    public boolean save(Room room) {
        dataManager.saveRoom(room);
        return true;
    }

    @Override
    public boolean update(Room room) {
        dataManager.getRooms().put(room.getId(), room);
        return true;
    }

    @Override
    public boolean delete(int id) {
        dataManager.deleteRoom(id);
        return true;
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        List<Room> rooms = new ArrayList<>();
        dataManager.getRooms().values().forEach(room -> {
            if (room.getStatus() == RoomStatus.AVAILABLE)
                rooms.add(room);
        });
        rooms.sort(filter.getComparator());
        return rooms;
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        List<Room> rooms = new ArrayList<>(dataManager.getRooms().values().stream().toList());
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
