package hotel.service;

import hotel.annotation.Component;
import hotel.annotation.Inject;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component("modifiableRoomService")
public class ModifiableRoomService implements ModifiableIRoomService {
    @Inject
    protected RoomRepository roomRepository;
    @Inject
    protected BookingsRepository bookingsRepository;
    @Inject
    protected TransactionManager transactionManager;
    @Inject
    private ReadRoomService readRoomService;
    private static final Logger log = LoggerFactory.getLogger(ModifiableRoomService.class);

    public ModifiableRoomService() {
        super();
    }

    @Override
    public void changeRoomPrice(Integer idRoom, BigDecimal newPrice) {
        log.info("changeRoomPrice() for room: {}, new price: {}", idRoom, newPrice);

        transactionManager.executeInTransaction(() -> {
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
            return null;
        });
    }

    @Override
    public void addRoom(Room room) {
        log.info("addRoom() for room: {}", room);

        transactionManager.executeInTransaction(() -> {
            if (roomRepository.findById(room.getId()).isPresent()) {
                throw new HotelException(ErrorCode.ROOM_ALREADY_EXISTS,
                        "Комната с номером " + room.getId() + " уже существует");
            }

            Integer savedId = roomRepository.save(room);
            Room savedRoom = roomRepository.findById(savedId)
                    .orElseThrow(() -> new HotelException(ErrorCode.ROOM_NOT_FOUND,
                            "Не удалось найти сохраненную комнату"));

            System.out.println("Админ добавил новую комнату: " + savedRoom);
            log.info("addRoom(): added room with id: {}", savedId);
            return null;
        });
    }

    @Override
    public void update(Room room) {
        log.info("update() for room: {}", room);

        transactionManager.executeInTransaction(() -> {
            if (!roomRepository.findById(room.getId()).isPresent()) {
                throw new HotelException(ErrorCode.ROOM_NOT_FOUND,
                        "Комната не найдена для обновления");
            }

            roomRepository.update(room);
            log.info("update()");
            return null;
        });
    }

    @Override
    public void delete(Room room) {
        log.info("delete() for room: {}", room);

        transactionManager.executeInTransaction(() -> {
            roomRepository.delete(room);
            log.info("delete()");
            return null;
        });
    }

    @Override
    public List<Room> listAvailableRooms(RoomFilter filter) {
        return readRoomService.listAvailableRooms(filter);
    }

    @Override
    public List<Room> listAvailableRoomsByDate(RoomFilter filter, LocalDate date) {
        return readRoomService.listAvailableRoomsByDate(filter, date);
    }

    @Override
    public List<Room> sortRooms(RoomFilter filter) {
        return readRoomService.sortRooms(filter);
    }

    @Override
    public void requestListRoomAndPrice(RoomFilter filter) {
        readRoomService.requestListRoomAndPrice(filter);
    }

    @Override
    public void setTotalPrice(Integer roomId, BigDecimal newPrice) throws SQLException {
        readRoomService.setTotalPrice(roomId, newPrice);
    }

    @Override
    public void setStatusRoom(Integer roomId, RoomStatus status) {
        log.info("setStatusRoom() for room: {}, status: {}", roomId, status);

        transactionManager.executeInTransaction(() -> {
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
            return null;
        });
    }

    @Override
    public Optional<Room> findById(Integer id) throws SQLException {
        return readRoomService.findById(id);
    }

    @Override
    public List<Room> findAll() {
        return readRoomService.findAll();
    }

    @Override
    public Integer save(Room room) {
        log.info("save() for room: {}", room);

        return transactionManager.executeInTransaction(() -> {
            Integer result = roomRepository.save(room);
            log.info("save(): saved room with id: {}", result);
            return result;
        });
    }
}