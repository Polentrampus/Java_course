package hotel.view.action.booking;

import hotel.controller.AdminController;
import hotel.dto.CreateBookingRequest;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.room.RoomStatus;
import hotel.model.users.client.Client;
import hotel.view.action.BaseAction;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CreateBookingAction extends BaseAction {
    private final AdminController adminController;

    public CreateBookingAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СОЗДАНИЕ НОВОГО БРОНИРОВАНИЯ ===");

            Collection<Client> clients = adminController.getInfoAboutClientDatabase(null);
            if (clients.isEmpty()) {
                System.out.println("Нет доступных клиентов для бронирования.");
                return;
            }

            System.out.println("\nДоступные клиенты:");
            clients.forEach(client ->
                    System.out.println("ID: " + client.getId() + " | " + client.getName()));

            int clientId = readInt("Введите ID клиента: ");

            // Выбор комнаты
            System.out.println("\nДоступные комнаты:");
            List<Room> availableRooms = adminController.requestListRoom(RoomFilter.ID).stream()
                    .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                    .collect(Collectors.toList());

            if (availableRooms.isEmpty()) {
                System.out.println("Нет доступных комнат для бронирования.");
                return;
            }

            availableRooms.forEach(room ->
                    System.out.println("ID: " + room.getId() + " | №" + room.getNumber() +
                            " | Тип: " + room.getType() + " | Цена: $" + room.getPrice()));

            int roomId = readInt("Введите ID комнаты: ");

            LocalDate checkInDate = readDate("Введите дату заезда");
            LocalDate checkOutDate = readDate("Введите дату выезда");

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

            var result = adminController.createBooking(request);
            if (result.isPresent()) {
                System.out.println("\nБронирование успешно создано!");
            } else {
                System.out.println("Не удалось создать бронирование.");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при создании бронирования: " + e.getMessage());
        }
    }
}
