package hotel.repository.room;

import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryRoomRepository implements RoomRepository {
    private Map<Integer, Room> roomMap = new HashMap<>();

    public InMemoryRoomRepository() {
        initializeTestData();
    }

    private void initializeTestData() {
        roomMap.put(100, new Room(100, RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.SUITE, 2, BigDecimal.valueOf(1000)));
        roomMap.put(101, new Room(101, RoomCategory.BUSINESS, RoomStatus.AVAILABLE, RoomType.STANDARD, 1, BigDecimal.valueOf(3000)));
        roomMap.put(102, new Room(102, RoomCategory.PREMIUM, RoomStatus.OCCUPIED, RoomType.FAMILY, 2, BigDecimal.valueOf(4000)));
        roomMap.put(200, new Room(200, RoomCategory.ECONOMY, RoomStatus.MAINTENANCE, RoomType.APARTMENT, 3, BigDecimal.valueOf(2000)));
        roomMap.put(201, new Room(201, RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.STANDARD, 2, BigDecimal.valueOf(1500)));
        roomMap.put(202, new Room(202, RoomCategory.BUSINESS, RoomStatus.OCCUPIED, RoomType.PRESIDENTIAL, 4, BigDecimal.valueOf(2300)));
        roomMap.put(302, new Room(302, RoomCategory.PREMIUM, RoomStatus.AVAILABLE, RoomType.FAMILY, 2, BigDecimal.valueOf(13000)));
        roomMap.put(300, new Room(300, RoomCategory.ECONOMY, RoomStatus.AVAILABLE, RoomType.APARTMENT, 1, BigDecimal.valueOf(14000)));
        roomMap.put(301, new Room(301, RoomCategory.PREMIUM, RoomStatus.OCCUPIED, RoomType.STANDARD, 2, BigDecimal.valueOf(2000)));
        roomMap.put(303, new Room(303, RoomCategory.BUSINESS, RoomStatus.AVAILABLE, RoomType.PRESIDENTIAL, 1, BigDecimal.valueOf(4000)));
    }

    @Override
    public Optional<Room> findById(int number) throws SQLException {
        return Optional.ofNullable(roomMap.get(number));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(roomMap.values());
    }

    @Override
    public boolean save(Room room) {
        roomMap.put(room.getNumber(), room);
        return true;
    }

    @Override
    public boolean update(Room room) {
        roomMap.put(room.getNumber(), room);
        return true;
    }

    @Override
    public boolean delete(int id) {
        roomMap.remove(id);
        return true;
    }

    public List<Room> listAvailableRooms(RoomFilter filter) {
        List<Room> roomsAvailable = new ArrayList<>();
        sortRooms(filter);
        for (Room room : roomMap.values()) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                roomsAvailable.add(room);
            }
        }
        return roomsAvailable;
    }

    public List<Room> sortRooms(RoomFilter filter) {
        return roomMap.values().stream()
                .sorted(filter.getComparator())
                .collect(Collectors.toList());
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        System.out.println("Номер комнаты/цена");
        for (Room room : sortRooms(filter)) {
            System.out.println(room.getNumber() + " " + room.getPrice());
        }
    }
}
