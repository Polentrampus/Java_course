package hotel.controller.manager;

import hotel.model.Hotel;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.users.employee.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoomManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public RoomManager(Employee employee) {
        this.employee = employee;
    }

    public List<Room> listAvailableRooms(RoomFilter filter){
        List<Room> roomsAvailable = new ArrayList<>();
        sortRooms(filter);
        for (Room room : hotel.getRoomMap().get().values()){
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                roomsAvailable.add(room);
                //System.out.println(room.toString());
            }
        }
//        System.out.println("Общее число свободных номеров= " + roomsAvailable.size());
        return roomsAvailable;
    }

    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date){
        List<Room> roomsAvailable = listAvailableRooms(filter);
        List<Room> result = new ArrayList<>();
        if (roomsAvailable.isEmpty() && roomsAvailable == null) {
            return result;
        }

        //System.out.printf("Список свободных к %tF дате комнат:\n", date);
        for (Room room : roomsAvailable) {
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                result.add(room);
                //System.out.println(room.toString());
            }
        }
        return result;
    }

    public List<Room> sortRooms(RoomFilter filter){
        List<Room> roomList = new ArrayList<>();
        if(hotel.getRoomMap().get().values().isEmpty()){
            return roomList;
        }
        roomList = hotel.getRoomMap().get().values().stream()
                .sorted(filter.getComparator())
                .collect(Collectors.toList());;
        return roomList;
    }

    public void changeRoomPrice(int idRoom, int newPrice){
        System.out.println(employee.getPosition() + " изменил цену комнаты номер: " + idRoom +
                " \nс "+hotel.getRoomMap().get().get(idRoom).getPrice() + " на " + newPrice);
        hotel.getRoomMap().get().get(idRoom).setPrice(newPrice);
    }

    public void addRoom(RoomCategory category, RoomStatus status, RoomType type, int capacity, int roomNumber, int price){
        Room room = new Room(category, status, type, roomNumber, price, capacity);
        hotel.getRoomMap().get().put(roomNumber, room);
        room.setStatus(status);
        System.out.println("Админ добавил новую комнату: " + hotel.getRoomMap().get().get(roomNumber).toString());
    }

    public void requestListRoom(RoomFilter filter){
        for (Room room : sortRooms(filter)){
            System.out.println(room.toString());
        }
    }

    public void requestListRoomAndPrice(RoomFilter filter){
        System.out.println("Номер комнаты/цена");
        for (Room room : sortRooms(filter)){
            System.out.println(room.getNumber() + " " + room.getPrice());
        }
    }

    public void setStatusRoom(int roomId, RoomStatus status) {
        System.out.printf("%s изменил состояние комнаты %d с %s на %s\n", employee.getPosition(), roomId, hotel.getRoomMap().get().get(roomId).getStatus(), status);
        hotel.getRoomMap().get().get(roomId).setStatus(status);
    }

}
