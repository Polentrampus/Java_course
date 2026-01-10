package hotel.service;

import hotel.annotation.Component;
import hotel.model.Hotel;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class RoomService implements IRoomService {
    protected final Hotel hotel = Hotel.getInstance();

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        if (hotel.getRoomMap().isEmpty()) {
            return Collections.emptyList();
        }
        List<Room> roomsAvailable = new ArrayList<>();
        sortRooms(filter);

        for (Room room : hotel.getRoomMap().get().values()) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                roomsAvailable.add(room);
            }
        }
        return roomsAvailable;
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        List<Room> roomsAvailable = listAvailableRooms(filter);
        List<Room> result = new ArrayList<>();
        if (roomsAvailable.isEmpty()) {
            return Collections.emptyList();
        }
        for (Room room : roomsAvailable) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                result.add(room);
            }
        }
        return result;
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        List<Room> roomList = new ArrayList<>();
        if (hotel.getRoomMap().isEmpty()) {
            return Collections.emptyList();
        }
        roomList = hotel.getRoomMap().get().values().stream()
                .sorted(filter.getComparator())
                .collect(Collectors.toList());
        ;
        return roomList;
    }


    public List<Room> requestListRoom(RoomFilter filter) {
        for (Room room : sortRooms(filter)) {
            System.out.println(room.toString());
        }
        return sortRooms(filter);
    }

    public void requestListRoomAndPrice(RoomFilter filter) {
        System.out.println("Номер комнаты/цена");
        for (Room room : sortRooms(filter)) {
            System.out.println(room.getNumber() + " " + room.getPrice());
        }
    }

}
