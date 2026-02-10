package hotel.view.action.booking;

import hotel.dto.CreateBookingRequest;
import hotel.model.booking.BookingStatus;
import hotel.model.booking.Bookings;
import hotel.model.filter.RoomFilter;
import hotel.model.room.Room;
import hotel.model.service.Services;
import hotel.model.users.client.Client;
import hotel.service.ClientService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.service.ServicesService;
import hotel.view.action.BaseAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CreateBookingAction extends BaseAction {
    private final ClientService clientService;
    private final IRoomService roomService;
    private final IBookingService bookingService;
    private final ServicesService serviceService;

    public CreateBookingAction(ClientService clientService,
                               IRoomService roomService,
                               IBookingService bookingService,
                               ServicesService serviceService,
                               Scanner scanner) {
        super(scanner);
        this.clientService = clientService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.serviceService = serviceService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== СОЗДАНИЕ НОВОГО БРОНИРОВАНИЯ ===");

            List<Client> clients = clientService.findAll();
            if (clients.isEmpty()) {
                System.out.println("Нет доступных клиентов для бронирования.");
                return;
            }

            System.out.println("\nДоступные клиенты:");
            clients.forEach(client ->
                    System.out.println("ID: " + client.getId() + " | " + client.getName()));

            int clientId = readInt("Введите ID клиента: ");

            LocalDate checkInDate = readDate("Введите дату заезда");
            LocalDate checkOutDate = readDate("Введите дату выезда");

            System.out.println("\nДоступные комнаты:");
            List<Room> availableRooms = roomService.listAvailableRoomsByDate(RoomFilter.ID, checkInDate);

            if (availableRooms.isEmpty()) {
                System.out.println("Нет доступных комнат для бронирования.");
                return;
            }

            availableRooms.forEach(room ->
                    System.out.println("ID: " + room.getId() + " | №" + room.getNumber() +
                            " | Тип: " + room.getType() + " | Цена: $" + room.getPrice()));

            int roomId = readInt("Введите ID комнаты: ");


            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                System.out.println("Дата выезда должна быть после даты заезда!");
                return;
            }

            List<Services> services = new ArrayList<>();
            List<Services> allServices = serviceService.findAll();

            System.out.println("\nДоступные услуги:");
            for (int i = 0; i < allServices.size(); i++) {
                Services service = allServices.get(i);
                System.out.println(i + ": " + service.getName() + " - $" + service.getPrice());
            }
            System.out.println("Чтобы закончить выбор, введите -1");

            while (true) {
                int serviceIndex = readInt("Введите номер услуги: ");
                if (serviceIndex == -1) break;

                if (serviceIndex < 0 || serviceIndex >= allServices.size()) {
                    System.out.println("Такого номера услуги не существует!");
                    continue;
                }
                services.add(allServices.get(serviceIndex));
            }

            CreateBookingRequest request = new CreateBookingRequest(
                    clientId, roomId, checkInDate, checkOutDate, services, BookingStatus.CONFIRMED
            );

            Optional<Bookings> result = bookingService.createBooking(request);
            if (result.isPresent()) {
                System.out.println("\nБронирование успешно создано!");
                System.out.println(result.get().toString());
            } else {
                System.out.println("Не удалось создать бронирование.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при создании бронирования: " + e.getMessage());
        }
    }
}