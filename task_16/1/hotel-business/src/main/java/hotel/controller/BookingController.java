package hotel.controller;

import hotel.dto.BookingDTO;
import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;
import hotel.service.IBookingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final IBookingService bookingService;

    @GetMapping("/findAll")
    @Operation(
            summary = "Получить все бронирования",
            description = "Возвращает список всех бронирований."
    )
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        log.info("Получение списка всех бронирований");
        List<Bookings> bookings = bookingService.getAllBookings();
        List<BookingDTO> responses = bookings.stream().
                map(BookingDTO::from).toList();
        log.debug("Найдено бронирований: {}", bookings.size());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(
            summary = "Получить активные бронирования",
            description = "Возвращает список бронирований, у которых дата выезда еще не наступила."
    )
    public ResponseEntity<List<BookingDTO>> getActiveBookings() {
        List<Bookings> bookings = bookingService.findActiveBookings(LocalDate.now());
        List<BookingDTO> responses = bookings.stream().
                map(BookingDTO::from).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить бронирование по ID",
            description = "Возвращает детальную информацию о конкретном бронировании."
    )
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable(name = "id") Integer id) throws SQLException {
        Optional<Bookings> booking = bookingService.getBookingById(id);
        if (booking.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(booking.map(BookingDTO::from).get());
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Получить бронирования клиента",
            description = "Возвращает все бронирования конкретного клиента."
    )
    public ResponseEntity<List<BookingDTO>> getClientBookings(@PathVariable(name = "clientId") Integer clientId) {
        List<Bookings> bookings = bookingService.findActiveByClientId(clientId);
        List<BookingDTO> responses = bookings.stream().
                map(BookingDTO::from).toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/createBooking")
    @Operation(
            summary = "Создать новое бронирование",
            description = """
            Создание нового бронирования номера.
            
            ### Проверки:
            1. Комната должна быть доступна (статус AVAILABLE)
            2. На выбранные даты комната не должна быть забронирована
            3. Даты должны быть в будущем
            4. Дата выезда должна быть после даты заезда
            
            ### Пример JSON:
            ```json
            {
              "clientId": 1,
              "roomId": 101,
              "checkInDate": "2024-12-25",
              "checkOutDate": "2024-12-31"
            }
            ```
            """
    )
    public ResponseEntity<BookingDTO> createBooking(
            @Valid @RequestBody CreateBookingRequest request) throws SQLException {
        log.info("Начало создания бронирования для клиента ID: {}, комната ID: {}",
                request.getClientId(), request.getRoomId());
        Optional<Bookings> booking = bookingService.createBooking(request);
        log.info("Бронирование успешно создано. ID: {}, клиент: {}, комната: {}",
                booking.get().getId(), booking.get().getClient().getId(), booking.get().getRoom().getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/bookings/" + booking.get().getId())
                .body(booking.map(BookingDTO::from).get());
    }

    @PostMapping("/{bookingId}/services/{serviceId}")
    @Operation(
            summary = "Добавить услугу к бронированию",
            description = "Добавляет дополнительную услугу к существующему бронированию."
    )
    public ResponseEntity<BookingDTO> addServiceToBooking(
            @PathVariable(name = "bookingId") Integer bookingId,
            @PathVariable(name = "serviceId") List<Integer> serviceId) throws SQLException {
        Optional<Bookings> booking = bookingService.addServiceToBooking(bookingId, serviceId);
        if (booking.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(booking.map(BookingDTO::from).get());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить бронирование",
            description = """
            Удаление бронирования по ID.
            Если дата выезда еще не наступила, комната возвращается в статус AVAILABLE.
            """
    )
    public ResponseEntity<Void> deleteBooking(@PathVariable(name = "id") Integer id) throws SQLException {
        log.info("Удаление бронирования ID: {}", id);
        bookingService.deleteBookingById(id);
        return ResponseEntity.status(HttpStatus.OK).
                header("Delete", "/bookings/" + id).
                build();
    }
}