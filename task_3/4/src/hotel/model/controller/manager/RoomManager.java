package hotel.model.controller.manager;

import hotel.model.Hotel;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.personal.employee.Employee;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    protected final Hotel hotel = Hotel.getInstance();
    Employee employee;

    public List<Room> listAvailableRooms(){
        List<Room> roomsAvailable = new ArrayList<>();
        for (Room room : hotel.getRoomMap().values()){
            if (room.getStatus() == RoomStatus.AVAILABLE) {
                roomsAvailable.add(room);
                //System.out.println(room.toString());
            }
        }
        return roomsAvailable;
    }
}
