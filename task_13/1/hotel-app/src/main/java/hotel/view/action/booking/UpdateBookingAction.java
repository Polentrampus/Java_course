package hotel.view.action.booking;

import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.model.users.client.Client;
import hotel.service.ClientService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class UpdateBookingAction extends BaseAction {
    private final ClientService clientService;
    private final IRoomService roomService;
    private final IBookingService bookingService;

    public UpdateBookingAction(ClientService clientService,
                               IRoomService roomService,
                               IBookingService bookingService,
                               Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ОБНОВЛЕНИЕ БРОНИРОВАНИЯ ===");

            int bookingId = readInt("Введите ID бронирования для обновления: ");

            Optional<Bookings> existingBooking = bookingService.getBookingById(bookingId);
            if (existingBooking.isEmpty()) {
                System.out.println("Бронирование с ID " + bookingId + " не найдено.");
                return;
            }

            Bookings booking = existingBooking.get();
            System.out.println("\nТекущие данные бронирования:");
            printBookingSummary(booking);

            System.out.println("\n--- ОБНОВЛЕНИЕ КЛИЕНТА ---");
            System.out.println("Текущий клиент: " + clientService.findById(booking.getClient().getId()).get());

            System.out.println("\nВыберите нового клиента (оставьте пустым, чтобы не менять):");
            Collection<Client> clients = clientService.findAll();
            clients.forEach(client ->
                    System.out.println("ID: " + client.getId() + " | " +
                            client.getSurname() + " " + client.getName() + " " + client.getPatronymic()));

            String clientInput = readString("Введите ID клиента или нажмите Enter: ");
            int clientId = clientInput.isEmpty() ?
                    booking.getClient().getId(): Integer.parseInt(clientInput);

            System.out.println("\n--- ОБНОВЛЕНИЕ КОМНАТЫ ---");
            System.out.println("Текущая комната: №" + booking.getRoom());

            System.out.println("\nВыберите новую комнату (оставьте пустым, чтобы не менять):");
            List<Room> availableRooms = roomService.findAll().stream()
                    .filter(room -> room.getStatus() == RoomStatus.AVAILABLE ||
                            Objects.equals(room.getId(), booking.getRoom()))
                    .toList();

            if (availableRooms.isEmpty()) {
                System.out.println("Нет доступных комнат для замены.");
                System.out.println("Сохранена текущая комната.");
            } else {
                availableRooms.forEach(room ->
                        System.out.println("ID: " + room.getId() + " | №" + room.getNumber() +
                                " | Тип: " + room.getType() +
                                " | Цена: $" + room.getPrice() +
                                " | Статус: " + room.getStatus()));
            }

            String roomInput = readString("Введите ID комнаты или нажмите Enter: ");
            int roomId = roomInput.isEmpty() ? booking.getRoom().getId() : Integer.parseInt(roomInput);

            System.out.println("\n--- ОБНОВЛЕНИЕ ДАТ ПРЕБЫВАНИЯ ---");
            System.out.println("Текущие даты: " + booking.getCheckInDate() + " - " + booking.getCheckOutDate());

            LocalDate checkInDate = booking.getCheckInDate();
            String checkInInput = readString("Дата заезда [" + checkInDate + "] (оставьте пустым чтобы не менять): ");
            if (!checkInInput.isEmpty()) {
                checkInDate = LocalDate.parse(checkInInput);
            }

            LocalDate checkOutDate = booking.getCheckOutDate();
            String checkOutInput = readString("Дата выезда [" + checkOutDate + "] (оставьте пустым чтобы не менять): ");
            if (!checkOutInput.isEmpty()) {
                checkOutDate = LocalDate.parse(checkOutInput);
            }

            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                System.out.println("Дата выезда должна быть после даты заезда!");
                return;
            }

            CreateBookingRequest request = new CreateBookingRequest(
                    clientId, roomId, checkInDate, checkOutDate,
                    booking.getServices(), booking.getStatus()
            );

            Optional<Bookings> result = bookingService.updateBooking(request, bookingId);

            if (result.isPresent()) {
                System.out.println("\nБронирование успешно обновлено!");
                System.out.println("\nОбновленные данные:");
                printBookingSummary(result.get());
            } else {
                System.out.println("Не удалось обновить бронирование.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении бронирования: " + e.getMessage());
        }
    }

    private void printBookingSummary(Bookings booking) throws SQLException {
        System.out.println("ID бронирования: " + booking.getId());
        System.out.println("Клиент: " + clientService.findById(booking.getClient().getId()).get());
        System.out.println("Комната: №" + booking.getRoom());
        System.out.println("Дата заезда: " + booking.getCheckInDate());
        System.out.println("Дата выезда: " + booking.getCheckOutDate());
        System.out.println("Статус: " + booking.getStatus());
        System.out.println("Общая стоимость: $" + booking.getTotalPrice());
    }
}