package hotel.service;

import hotel.annotation.Component;
import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;

@Component
public class ModifiableRoomService extends RoomService implements ModifiableIRoomService{

    @Override
    public void changeRoomPrice(int idRoom, int newPrice) {
        if (hotel.getRoomMap().isEmpty()) {
            System.out.println("указанного номера нет в базе данных.");
            return;
        }
        if(hotel.getRoomMap().get().get(idRoom) == null){
            throw new RoomNotFoundException(idRoom);
        }

        System.out.println( "цена комнаты номер: " + idRoom +
                " \nизменилась с " + hotel.getRoomMap().get().get(idRoom).getPrice() + " на " + newPrice);
        hotel.getRoomMap().get().get(idRoom).setPrice(newPrice);
    }

    @Override
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

    @Override
    public void setStatusRoom(int roomId, RoomStatus status) {
        if (hotel.getRoomMap().isEmpty()) {
            System.out.println(roomId + " - указанный номер не был найден.");
            return;
        }
        if(hotel.getRoomMap().get().get(roomId) == null){
            throw new RoomNotFoundException(roomId);
        }
        System.out.printf("состояние комнаты %d изменилось с %s на %s\n", roomId, hotel.getRoomMap().get().get(roomId).getStatus(), status);
        hotel.getRoomMap().get().get(roomId).setStatus(status);
    }
}
