package hotel.service;

import hotel.annotation.Component;
import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomNotFoundException;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomCategory;
import hotel.model.room.RoomStatus;
import hotel.model.room.RoomType;
import hotel.repository.HotelRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Component
public class ModifiableRoomService extends RoomService implements ModifiableIRoomService{

    @Override
    public void changeRoomPrice(int idRoom, BigDecimal newPrice) throws SQLException {
        System.out.println( "цена комнаты номер: " + idRoom +
                " \nизменилась с " + roomRepository.findById(idRoom).get().getPrice() + " на " + newPrice);
        roomRepository.findById(idRoom).get().setPrice(newPrice);
    }

    @Override
    public void addRoom(Room room) throws SQLException {
        roomRepository.save(room);
        System.out.println("Админ добавил новую комнату: " + roomRepository.findById(room.getId()).get());
    }

    @Override
    public void setStatusRoom(int roomId, RoomStatus status) throws SQLException {
        System.out.printf("состояние комнаты %d изменилось с %s на %s\n", roomId,
               roomRepository.findById(roomId).get().getStatus(), status);
        roomRepository.findById(roomId).get().setStatus(status);
    }
}
