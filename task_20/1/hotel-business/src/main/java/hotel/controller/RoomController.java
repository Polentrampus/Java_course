package hotel.controller;

import hotel.dto.CreateRoomRequest;
import hotel.dto.RoomDto;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.service.IRoomService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController // (комбинирует @Controller и @ResponseBody, что позволяет возвращать объекты напрямую в виде JSON)
@RequestMapping("/rooms") // Базовый URL для всех методов этого контроллера
@RequiredArgsConstructor
public class RoomController {

    private final IRoomService roomService;

    @GetMapping("/public/findAll")
    @Operation(
            summary = "Получить список комнат",
            description = "Выводит список комнат сортируя их."
    )
    public ResponseEntity<List<RoomDto>> getAllRooms(
            @RequestParam(name = "sortBy", required = false) String sortBy) {
        log.info("Получение списка всех комнат, сортировка: {}",
                sortBy != null ? sortBy : "без сортировки");
        RoomFilter filter;
        if(sortBy == null) {
            filter = RoomFilter.ID;
        }else
            filter = RoomFilter.valueOf(sortBy);
        List<Room> rooms = roomService.sortRooms(filter);
        List<RoomDto> responses = rooms.stream()
                .map(RoomDto::from)
                .toList();
        log.debug("Найдено комнат: {}", rooms.size());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/public/available")
    @Operation(summary = "Получить список доступных комнат к дате")
    public ResponseEntity<List<RoomDto>> getAvailableRooms(
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Получение доступных комнат на дату: {}, сортировка: {}", date, sortBy);

        RoomFilter filter;
        if(sortBy == null) {
            filter = RoomFilter.ID;
        }else
            filter = RoomFilter.valueOf(sortBy);

        List<Room> rooms = roomService.listAvailableRoomsByDate(filter, date);
        List<RoomDto> responses = rooms.stream()
                .map(RoomDto::from)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/createRoom")
    @Operation(
            summary = "Создать новую комнату",
            description = """
            Создание нового номера в отеле.
            
            ### Статус по умолчанию:
            При создании комната получает статус **AVAILABLE** (доступна).
            
            ### Пример JSON запроса:
            ```json
            {
              "roomCategory": "BUSINESS",
              "roomStatus": "AVAILABLE",
              "roomType": "DELUXE",
              "price": 7500.0,
              "capacity": 3
            }
            ```
          
            """
    )
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request) throws SQLException {
        log.info("Создание новой комнаты: Категория={}, Тип={}, Цена={}, Вместимость={}",
                request.getRoomCategory(), request.getRoomType(),
                request.getPrice(), request.getCapacity());
        log.debug("Данные комнаты: {}", request);

        Optional<Room> room = roomService.addRoom(request);
        if(room.isEmpty()) return ResponseEntity.badRequest().build();
        log.info("Комната успешно создана: ID={}, Категория={}",
                room.get().getId(), room.get().getCategory());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/rooms/" + room.get().getId())
                .body(room.map(RoomDto::from).get());
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Обновить статус комнаты",
            description = """
            Изменение статуса комнаты.
            ### Валидные статусы:
            - "AVAILABLE"
            - "OCCUPIED"
            - "CLEANING"
            - "MAINTENANCE"
            """
    )
    /**
     * @PathVariable - извлекает значение из URL (например, /rooms/5/status - id будет 5)
     * @RequestBody - извлекает данные из тела HTTP запроса (например, JSON с новым статусом комнаты)
     */
    public ResponseEntity<Void> updateRoomStatus(
            @PathVariable(name = "id") Integer id,
            @Valid @RequestBody RoomStatus status) throws SQLException {
        log.info("Обновление статуса комнаты ID: {} на: {}", id, status);
        roomService.setStatusRoom(id, status);
        log.info("Статус комнаты ID: {} успешно обновлен на: {}", id, status);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/price")
    @Operation(
            summary = "Обновить цену комнаты",
            description = """
            Изменение цены за сутки для комнаты.
            """
    )
    public ResponseEntity<Void> updateRoomPrice(
            @PathVariable(name = "id") Integer id,
            @Valid @RequestBody BigDecimal price) throws SQLException {
        log.info("Обновление цены комнаты ID: {} на: {}", id, price);
        Optional<Room> room = roomService.findById(id);
        if(room.isEmpty() || (price.compareTo(BigDecimal.ZERO) <= 0)) {
            log.warn("Не удалось обновить цену комнаты ID: {}. Комната не найдена или цена некорректная: {}",
                    id, price);
            return ResponseEntity.badRequest().build();
        }
        roomService.setTotalPrice(id, price);
        log.info("Цена комнаты ID: {} успешно обновлена на: {}", id, price);
        return ResponseEntity.noContent().build();
    }
}