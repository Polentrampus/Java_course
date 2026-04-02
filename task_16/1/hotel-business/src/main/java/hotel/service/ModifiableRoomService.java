package hotel.service;

import hotel.dto.CreateRoomRequest;
import hotel.exception.ErrorCode;
import hotel.exception.HotelException;
import hotel.exception.room.RoomAlreadyExistsException;
import hotel.exception.room.RoomNotFoundException;
import hotel.exception.room.RoomNotAvailableException;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.repository.booking.BookingsRepository;
import hotel.repository.room.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
public class ModifiableRoomService extends ReadRoomService implements ModifiableIRoomService {
    private static final Logger log = LoggerFactory.getLogger(ModifiableRoomService.class);

    public ModifiableRoomService(RoomRepository roomRepository,
                                 BookingsRepository bookingsRepository) {
        super(roomRepository, bookingsRepository);
    }

    @Override
    public void changeRoomPrice(Integer idRoom, BigDecimal newPrice) {
        log.info("Changing price for room: {} to {}", idRoom, newPrice);

        try {
            // Валидация
            if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new HotelException(ErrorCode.ROOM_INVALID_DATA,
                        "Цена должна быть положительным числом")
                        .addDetail("roomId", idRoom)
                        .addDetail("providedPrice", newPrice);
            }

            Room room = roomRepository.findById(idRoom)
                    .orElseThrow(() -> new RoomNotFoundException(idRoom));

            // Проверка, можно ли менять цену (например, если комната занята)
            if (room.getStatus() == RoomStatus.OCCUPIED) {
                throw new RoomNotAvailableException(idRoom,
                        "Нельзя изменить цену занятой комнаты");
            }

            BigDecimal oldPrice = room.getPrice();
            room.setPrice(newPrice);
            roomRepository.update(room);

            log.info("Price for room {} changed from {} to {}", idRoom, oldPrice, newPrice);

        } catch (SQLException e) {
            log.error("Database error while changing room price", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при изменении цены комнаты", e);
        }
    }

    @Override
    public Optional<Room> addRoom(CreateRoomRequest request) {
        log.info("Adding new room: {}", request.getIdRoom());

        try {
            validateCreateRoomRequest(request);

            if (roomRepository.findById(request.getIdRoom()).isPresent()) {
                throw new RoomAlreadyExistsException(request.getIdRoom());
            }

            Room room = new Room();
            room.setNumber(request.getIdRoom());
            room.setType(request.getRoomType());
            room.setCapacity(request.getCapacity());
            room.setCategory(request.getRoomCategory());
            room.setPrice(request.getPrice());
            room.setStatus(request.getRoomStatus() != null ?
                    request.getRoomStatus() : RoomStatus.AVAILABLE);

            Integer savedId = roomRepository.save(room);

            Room savedRoom = roomRepository.findById(savedId)
                    .orElseThrow(() -> new HotelException(ErrorCode.DATA_INTEGRITY_VIOLATION,
                            "Не удалось найти сохраненную комнату"));

            log.info("Room added successfully with id: {}", savedId);
            return Optional.of(savedRoom);

        } catch (SQLException e) {
            log.error("Database error while adding room", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при добавлении комнаты", e);
        }
    }

    @Override
    public void update(Room room) {
        log.info("Updating room: {}", room);

        try {
            if (room == null || room.getId() == null) {
                throw new HotelException(ErrorCode.VALIDATION_ERROR,
                        "Некорректные данные комнаты для обновления");
            }

            if (roomRepository.findById(room.getId()).isEmpty()) {
                throw new RoomNotFoundException(room.getId());
            }

            roomRepository.update(room);
            log.info("Room updated successfully: {}", room.getId());

        } catch (SQLException e) {
            log.error("Database error while updating room", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при обновлении комнаты", e);
        }
    }

    @Override
    public void delete(Room room) {
        log.info("Deleting room: {}", room);

        try {
            if (room == null || room.getId() == null) {
                throw new HotelException(ErrorCode.VALIDATION_ERROR,
                        "Некорректные данные комнаты для удаления");
            }

            List<Bookings> activeBookings = bookingsRepository
                    .findActiveByRoomId(room.getId(), LocalDate.now());

            if (!activeBookings.isEmpty()) {
                throw new RoomNotAvailableException(room.getId(),
                        "Нельзя удалить комнату с активными бронированиями");
            }

            roomRepository.delete(room);
            log.info("Room deleted successfully: {}", room.getId());

        } catch (Exception e) {
            log.error("Database error while deleting room", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при удалении комнаты", e);
        }
    }

    @Override
    public void setStatusRoom(Integer roomId, RoomStatus status) {
        log.info("Setting status for room {} to {}", roomId, status);

        try {
            if (status == null) {
                throw new HotelException(ErrorCode.VALIDATION_ERROR,
                        "Статус комнаты не может быть null");
            }

            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RoomNotFoundException(roomId));

            RoomStatus oldStatus = room.getStatus();

            room.setStatus(status);
            roomRepository.update(room);

            log.info("Status for room {} changed from {} to {}",
                    roomId, oldStatus, status);

        } catch (SQLException e) {
            log.error("Database error while changing room status", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при изменении статуса комнаты", e);
        }
    }

    @Override
    public Integer save(Room room) {
        log.info("Saving room: {}", room);

        try {
            validateRoomForSave(room);

            Integer savedId = roomRepository.save(room);
            log.info("Room saved with id: {}", savedId);
            return savedId;

        } catch (Exception e) {
            log.error("Database error while saving room", e);
            throw new HotelException(ErrorCode.DATABASE_QUERY_ERROR,
                    "Ошибка при сохранении комнаты", e);
        }
    }

    private void validateCreateRoomRequest(CreateRoomRequest request) {
        if (request == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Запрос на создание комнаты не может быть пустым");
        }
        if (request.getIdRoom() == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Номер комнаты обязателен");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.ROOM_INVALID_DATA,
                    "Цена комнаты должна быть положительным числом");
        }
        if (request.getCapacity() <= 0) {
            throw new HotelException(ErrorCode.ROOM_INVALID_DATA,
                    "Вместимость комнаты должна быть положительным числом");
        }
    }

    private void validateRoomForSave(Room room) {
        if (room == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Комната не может быть null");
        }
        if (room.getNumber() == null) {
            throw new HotelException(ErrorCode.VALIDATION_ERROR,
                    "Номер комнаты обязателен");
        }
        if (room.getPrice() == null || room.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HotelException(ErrorCode.ROOM_INVALID_DATA,
                    "Цена комнаты должна быть положительным числом");
        }
    }
}