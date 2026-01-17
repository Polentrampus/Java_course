package hotel.service;

import hotel.model.Hotel;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RoomService implements IRoomService {
    protected final Hotel hotel = Hotel.getInstance();

    public RoomService() {
    }

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
        if (hotel.getRoomMap().isEmpty() || hotel.getBookingsMap().isEmpty()) {
            return Collections.emptyList();
        }
        List<Room> rooms = new ArrayList<>(hotel.getRoomMap().get().values().stream().toList());
        for (Bookings bookings : hotel.getBookingsMap().get().values()) {
            if (bookings.getCheckInDate().isBefore(date) && bookings.getCheckOutDate().isAfter(date)) {
                rooms.remove(bookings.getRoom());
            }
        }
        return rooms;
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
