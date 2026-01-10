package hotel.view.action.booking;

import hotel.controller.AdminController;
import hotel.dto.CreateBookingRequest;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.model.users.client.Client;
import hotel.view.action.BaseAction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UpdateBookingAction extends BaseAction {
    private final AdminController adminController;
    public UpdateBookingAction(Scanner scanner, AdminController adminController) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ОБНОВЛЕНИЕ БРОНИРОВАНИЯ ===");

            // Получение текущего бронирования
            int bookingId = readInt("Введите ID бронирования для обновления: ");

            Optional<Bookings> existingBooking = adminController.getBookingById(bookingId);
            if (existingBooking.isEmpty()) {
                System.out.println("Бронирование с ID " + bookingId + " не найдено.");
                return;
            }

            Bookings booking = existingBooking.get();
            System.out.println("\nТекущие данные бронирования:");
            printBookingDetails(booking);

            // Выбор клиента (можно оставить текущего)
            System.out.println("\nВыберите нового клиента (оставьте пустым, чтобы не менять):");
            Collection<Client> clients = adminController.getInfoAboutClientDatabase(null);
            clients.forEach(client ->
                    System.out.println("ID: " + client.getId() + " | " + client.getName()));

            String clientInput = readString("Введите ID клиента или нажмите Enter: ");
            int clientId = clientInput.isEmpty() ? booking.getClient().getId() : Integer.parseInt(clientInput);

            System.out.println("\nВыберите новую комнату (оставьте пустым, чтобы не менять):");
            List<Room> availableRooms = adminController.requestListRoom(RoomFilter.ID).stream()
                    .filter(room -> room.getStatus() == RoomStatus.AVAILABLE ||
                            room.getId() == booking.getRoom().getId())
                    .collect(Collectors.toList());

            availableRooms.forEach(room ->
                    System.out.println("ID: " + room.getId() + " | №" + room.getNumber() +
                            " | Тип: " + room.getType() + " | Цена: $" + room.getPrice()));

            String roomInput = readString("Введите ID комнаты или нажмите Enter: ");
            int roomId = roomInput.isEmpty() ? booking.getRoom().getId() : Integer.parseInt(roomInput);

            System.out.println("\nВведите новые даты (оставьте пустым, чтобы не менять):");

            LocalDate checkInDate = booking.getCheckInDate();
            String checkInInput = readString("Дата заезда [" + checkInDate + "]: ");
            if (!checkInInput.isEmpty()) {
                checkInDate = LocalDate.parse(checkInInput);
            }

            LocalDate checkOutDate = booking.getCheckOutDate();
            String checkOutInput = readString("Дата выезда [" + checkOutDate + "]: ");
            if (!checkOutInput.isEmpty()) {
                checkOutDate = LocalDate.parse(checkOutInput);
            }

            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                System.out.println("Дата выезда должна быть после даты заезда!");
                return;
            }

            CreateBookingRequest request = CreateBookingRequest.builder()
                    .clientId(clientId)
                    .roomId(roomId)
                    .checkInDate(checkInDate)
                    .checkOutDate(checkOutDate)
                    .build();

            var result = adminController.updateBooking(request, bookingId);

            if (result.isPresent()) {
                System.out.println("\nБронирование успешно обновлено!");
                printBookingDetails(result.get());
            } else {
                System.out.println("Не удалось обновить бронирование.");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при обновлении бронирования: " + e.getMessage());
        }
    }
    private void printBookingDetails(Bookings booking) {
        System.out.println("ID бронирования: " + booking.getId());
        System.out.println("Клиент: " + booking.getClient().getName() + " (ID: " + booking.getClient().getId() + ")");
        System.out.println("Комната: №" + booking.getRoom().getNumber() + " (ID: " + booking.getRoom().getId() + ")");
        System.out.println("Дата заезда: " + booking.getCheckInDate());
        System.out.println("Дата выезда: " + booking.getCheckOutDate());
        System.out.println("Общая стоимость: $" + booking.getTotalPrice());
    }
}
