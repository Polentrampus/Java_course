package hotel.view.action.booking;

import hotel.controller.AdminController;
import hotel.model.booking.Bookings;
import hotel.view.action.BaseAction;

import java.util.List;
import java.util.Scanner;

public class GetAllBookingsAction extends BaseAction {
    private final AdminController adminController;
    public GetAllBookingsAction(Scanner scanner, AdminController adminController) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ВСЕ БРОНИРОВАНИЯ ===");

            List<Bookings> bookings = adminController.findAllBookings();

            if (bookings.isEmpty()) {
                System.out.println("Бронирования не найдены.");
                return;
            }

            System.out.println("Всего бронирований: " + bookings.size());
            System.out.println("----------------------------------------");

            for (Bookings booking : bookings) {
                printBookingDetails(booking);
                System.out.println("----------------------------------------");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при получении списка бронирований: " + e.getMessage());
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
