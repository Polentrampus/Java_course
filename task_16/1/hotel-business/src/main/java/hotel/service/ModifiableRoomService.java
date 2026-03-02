package hotel.service;

import hotel.dto.CreateRoomRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@Transactional
public class ModifiableRoomService extends ReadRoomService implements ModifiableIRoomService {
    private static final Logger log = LoggerFactory.getLogger(ModifiableRoomService.class);

    public ModifiableRoomService(RoomRepository roomRepository,
                                 BookingsRepository bookingsRepository) {
        super(roomRepository, bookingsRepository);
    }

    @Override
    public void changeRoomPrice(Integer idRoom, BigDecimal newPrice) throws SQLException {
        log.info("changeRoomPrice() for room: {}, new price: {}", idRoom, newPrice);

        Room room = roomRepository.findById(idRoom)
                .orElseThrow(() -> new HotelException(ErrorCode.ROOM_NOT_FOUND,
                        "Комната не найдена с ID: " + idRoom));

        BigDecimal oldPrice = room.getPrice();
        room.setPrice(newPrice);
        roomRepository.update(room);

        System.out.println("Цена комнаты номер: " + idRoom +
                " \nизменилась с " + oldPrice + " на " + newPrice);

        log.info("changeRoomPrice(): changed price for room {} from {} to {}",
                idRoom, oldPrice, newPrice);
    }

    @Override
    public Optional<Room> addRoom(CreateRoomRequest request) throws SQLException {
        log.info("addRoom() for room: {}", request.getIdRoom());

        if (roomRepository.findById(request.getIdRoom()).isPresent()) {
            throw new HotelException(ErrorCode.ROOM_ALREADY_EXISTS,
                    "Комната с номером " + request.getIdRoom() + " уже существует");
        }
        Room room = new Room();
        room.setNumber(request.getIdRoom());
        room.setType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setCategory(request.getRoomCategory());
        room.setPrice(request.getPrice());

        Integer savedId = roomRepository.save(room);
        Room savedRoom = roomRepository.findById(savedId)
                .orElseThrow(() -> new HotelException(ErrorCode.ROOM_NOT_FOUND,
                        "Не удалось найти сохраненную комнату"));

        System.out.println("Админ добавил новую комнату: " + savedRoom);
        log.info("addRoom(): added room with id: {}", savedId);
        return Optional.of(savedRoom);
    }

    @Override
    public void update(Room room) throws SQLException {
        log.info("update() for room: {}", room);

        if (roomRepository.findById(room.getId()).isEmpty()) {
            throw new HotelException(ErrorCode.ROOM_NOT_FOUND,
                    "Комната не найдена для обновления");
        }

        roomRepository.update(room);
        log.info("update()");
    }

    @Override
    public void delete(Room room) {
        log.info("delete() for room: {}", room);
        roomRepository.delete(room);
        log.info("delete()");
    }


    @Override
    public void setStatusRoom(Integer roomId, RoomStatus status) throws SQLException {
        log.info("setStatusRoom() for room: {}, status: {}", roomId, status);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new HotelException(ErrorCode.ROOM_NOT_FOUND,
                        "Комната не найдена с ID: " + roomId));

        RoomStatus oldStatus = room.getStatus();
        room.setStatus(status);
        roomRepository.update(room);

        System.out.printf("Состояние комнаты %d изменилось с %s на %s\n",
                roomId, oldStatus, status);

        log.info("setStatusRoom(): changed status for room {} from {} to {}",
                roomId, oldStatus, status);
    }

    @Override
    public Integer save(Room room) {
        log.info("save() for room: {}", room);
        Integer result = roomRepository.save(room);
        log.info("save(): saved room with id: {}", result);
        return result;
    }
}