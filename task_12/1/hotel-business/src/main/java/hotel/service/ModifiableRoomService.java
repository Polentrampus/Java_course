package hotel.service;

import hotel.annotation.Component;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;

@Component("modifiableRoomService")
public class ModifiableRoomService extends RoomService implements ModifiableIRoomService {
    private static final Logger log = LoggerFactory.getLogger(ModifiableRoomService.class);

    @Override
    public void changeRoomPrice(int idRoom, BigDecimal newPrice) throws SQLException {
        log.info("changeRoomPrice()");
        System.out.println("цена комнаты номер: " + idRoom +
                " \nизменилась с " + roomRepository.findById(idRoom).get().getPrice() + " на " + newPrice);
        roomRepository.findById(idRoom).get().setPrice(newPrice);
        log.info("changeRoomPrice(): changed price for room " + idRoom + " to " + newPrice);
    }

    @Override
    public void addRoom(Room room) throws SQLException {
        log.info("addRoom()");
        roomRepository.save(room);
        System.out.println("Админ добавил новую комнату: " + roomRepository.findById(room.getId()).get());
        log.info("addRoom(): added room: " + room);
    }

    @Override
    public boolean update(Room room) {
        log.info("update()");
        boolean result = roomRepository.update(room);
        log.info("update(): result: " + result);
        return result;
    }

    @Override
    public boolean delete(int id) {
        log.info("delete()");
        boolean result = roomRepository.delete(id);
        log.info("delete(): result: " + result);
        return result;
    }

    @Override
    public void setStatusRoom(int roomId, RoomStatus status) throws SQLException {
        log.info("setStatusRoom()");
        System.out.printf("состояние комнаты %d изменилось с %s на %s\n", roomId,
                roomRepository.findById(roomId).get().getStatus(), status);
        roomRepository.findById(roomId).get().setStatus(status);
        log.info("setStatusRoom(): changed status for room " + roomId + " to " + status);
    }

    @Override
    public boolean save(Room room) throws SQLException {
        log.info("addRoom()");
        System.out.println("Админ добавил новую комнату: " + roomRepository.findById(room.getId()).get());
        log.info("addRoom(): added room: " + room);
        return roomRepository.save(room);
    }
}