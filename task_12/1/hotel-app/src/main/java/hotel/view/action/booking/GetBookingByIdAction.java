package hotel.view.action.booking;

import hotel.model.booking.Bookings;
import hotel.service.ClientService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class GetBookingByIdAction extends BaseAction {
    private final IBookingService bookingService;
    private final ClientService clientService;
    private final IRoomService roomService;

    public GetBookingByIdAction(IBookingService bookingService, Scanner scanner,
                                ClientService clientService, IRoomService roomService) {
        super(scanner);
        this.bookingService = bookingService;
        this.clientService = clientService;
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ПОИСК БРОНИРОВАНИЯ ПО ID ===");

            int id = readInt("Введите ID бронирования: ");

            Optional<Bookings> booking = bookingService.getBookingById(id);

            if (booking.isEmpty()) {
                System.out.println("Бронирование с ID " + id + " не найдено.");
                return;
            }

            System.out.println("\n══════════════════════════════════════════════════");
            System.out.println("НАЙДЕНО БРОНИРОВАНИЕ:");
            printBookingDetails(booking.get());
            System.out.println("══════════════════════════════════════════════════");
        } catch (Exception e) {
            System.out.println("Ошибка при поиске бронирования: " + e.getMessage());
        }
    }

    private void printBookingDetails(Bookings booking) throws SQLException {
        System.out.println("ID бронирования: " + booking.getId());
        System.out.println("Статус: " + booking.getStatus());
        System.out.println("\n--- ИНФОРМАЦИЯ О КЛИЕНТЕ ---");
        System.out.println("ID клиента: " + booking.getClient());
        System.out.println("ФИО: " + clientService.findById(booking.getClient()).get());
        System.out.println("Дата рождения: " +  clientService.findById(booking.getClient()).get().getDateOfBirth());

        System.out.println("\n--- ИНФОРМАЦИЯ О КОМНАТЕ ---");
        System.out.println("Номер: " + booking.getRoom());
        System.out.println("Описание: " + roomService.findById(booking.getRoom()).get());

        System.out.println("\n--- ДАТЫ ПРЕБЫВАНИЯ ---");
        System.out.println("Дата заезда: " + booking.getCheckInDate());
        System.out.println("Дата выезда: " + booking.getCheckOutDate());
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                booking.getCheckInDate(), booking.getCheckOutDate());
        System.out.println("Количество ночей: " + days);

        System.out.println("\n--- СТОИМОСТЬ ---");
        System.out.println("Стоимость проживания: $" +
                roomService.findById(booking.getRoom()).get().getPrice().multiply(java.math.BigDecimal.valueOf(days)));

        if (!booking.getServices().isEmpty()) {
            System.out.println("\n--- ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ---");
            booking.getServices().forEach(service ->
                    System.out.println("• " + service.getName() +
                            " - $" + service.getPrice() +
                            " (" + service.getDescription() + ")"));
        }

        System.out.println("\nОБЩАЯ СТОИМОСТЬ: $" + booking.getTotalPrice());
    }
}