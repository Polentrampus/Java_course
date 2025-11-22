package hotel.controller.manager;

import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.Hotel;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.users.employee.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoomManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public RoomManager(Employee employee) {
        this.employee = employee;
    }

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

    public void changeRoomPrice(int idRoom, int newPrice) {
        if (hotel.getRoomMap().isEmpty()) {
            System.out.println("указанного номера нет в базе данных.");
            return;
        }
        if(hotel.getRoomMap().get().get(idRoom) == null){
            throw new RoomNotFoundException(idRoom);
        }

        System.out.println(employee.getPosition() + " изменил цену комнаты номер: " + idRoom +
                " \nс " + hotel.getRoomMap().get().get(idRoom).getPrice() + " на " + newPrice);
        hotel.getRoomMap().get().get(idRoom).setPrice(newPrice);
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type, int capacity, int roomNumber, int price) {
        if (hotel.getRoomMap().isEmpty()) {
            System.out.println("База данных пуста.");
            return;
        }
        if(hotel.getRoomMap().get().get(roomNumber) != null){
            throw new RoomAlreadyExistsException(roomNumber);
        }
        Room room = new Room(roomNumber, category, status, type, price, capacity);
        hotel.getRoomMap().get().put(roomNumber, room);
        room.setStatus(status);
        System.out.println("Админ добавил новую комнату: " + hotel.getRoomMap().get().get(roomNumber).toString());
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

    public void setStatusRoom(int roomId, RoomStatus status) {
        if (hotel.getRoomMap().isEmpty()) {
            System.out.println(roomId + " - указанный номер не был найден.");
            return;
        }
        if(hotel.getRoomMap().get().get(roomId) == null){
            throw new RoomNotFoundException(roomId);
        }
        System.out.printf("%s изменил состояние комнаты %d с %s на %s\n", employee.getPosition(), roomId, hotel.getRoomMap().get().get(roomId).getStatus(), status);
        hotel.getRoomMap().get().get(roomId).setStatus(status);
    }

}
