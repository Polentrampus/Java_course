package hotel.view.action.booking;

import hotel.controller.AdminController;
import hotel.model.booking.Bookings;
import hotel.view.action.BaseAction;

import java.util.Optional;
import java.util.Scanner;

public class DeleteBookingByIdAction extends BaseAction {
    private final AdminController adminController;
        public DeleteBookingByIdAction(Scanner scanner, AdminController adminController) {
        super(scanner);
            this.adminController = adminController;
        }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== УДАЛЕНИЕ БРОНИРОВАНИЯ ===");

            int id = readInt("Введите ID бронирования для удаления: ");
            Optional<Bookings> booking = adminController.getBookingById(id);
            if (booking.isPresent()) {
                System.out.println("\nВы действительно хотите удалить это бронирование?");
                printBookingDetails(booking.get());

                String confirm = readString("Введите 'yes' для подтверждения: ");
                if (!confirm.equalsIgnoreCase("yes")) {
                    System.out.println("Удаление отменено.");
                    return;
                }

                adminController.deleteBooking(id);
                System.out.println("Бронирование успешно удалено!");
            } else {
                System.out.println("Бронирование с ID " + id + " не найдено.");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при удалении бронирования: " + e.getMessage());
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
