package hotel.view.action.booking;

import hotel.model.booking.Bookings;
import hotel.service.ClientService;
import hotel.service.IBookingService;
import hotel.service.IRoomService;
import hotel.view.action.BaseAction;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class DeleteBookingByIdAction extends BaseAction {
    private final IBookingService bookingService;
    private final ClientService clientService;
    private final IRoomService roomService;

    public DeleteBookingByIdAction(IBookingService bookingService,
                                   Scanner scanner, ClientService clientService,
                                   IRoomService roomService) {
        super(scanner);
        this.bookingService = bookingService;
        this.clientService = clientService;
        this.roomService = roomService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== УДАЛЕНИЕ БРОНИРОВАНИЯ ===");

            int id = readInt("Введите ID бронирования для удаления: ");

            Optional<Bookings> booking = bookingService.getBookingById(id);

            if (booking.isEmpty()) {
                System.out.println("Бронирование с ID " + id + " не найдено.");
                return;
            }

            System.out.println("\nВы действительно хотите удалить это бронирование?");
            printBookingDetails(booking.get());

            String confirm = readString("Введите 'yes' для подтверждения удаления: ");
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Удаление отменено.");
                return;
            }

            boolean isDeleted = bookingService.deleteBookingById(id);

            if (isDeleted) {
                System.out.println("Бронирование успешно удалено!");
            } else {
                System.out.println("Не удалось удалить бронирование.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при удалении бронирования: " + e.getMessage());
        }
    }

    private void printBookingDetails(Bookings booking) throws SQLException {
        System.out.println("ID бронирования: " + booking.getId());
        System.out.println("Клиент: " + clientService.findById(booking.getClient()).get());
        System.out.println("Комната: №" + roomService.findById(booking.getRoom()).get());
        System.out.println("Дата заезда: " + booking.getCheckInDate());
        System.out.println("Дата выезда: " + booking.getCheckOutDate());
        System.out.println("Статус: " + booking.getStatus());
        System.out.println("Общая стоимость: $" + booking.getTotalPrice());

        if (!booking.getServices().isEmpty()) {
            System.out.println("\nДополнительные услуги:");
            booking.getServices().forEach(service ->
                    System.out.println("  • " + service.getName() + " - $" + service.getPrice()));
        }
    }
}