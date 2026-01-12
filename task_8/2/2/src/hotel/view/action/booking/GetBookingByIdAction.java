package hotel.view.action.booking;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class GetBookingByIdAction extends BaseAction {
    private final AdminController adminController;
    public GetBookingByIdAction(Scanner scanner, AdminController adminController) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ПОИСК БРОНИРОВАНИЯ ПО ID ===");

            int id = readInt("Введите ID бронирования: ");

            var booking = adminController.getBookingById(id);

            if (booking.isEmpty()) {
                System.out.println("Бронирование с ID " + id + " не найдено.");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при поиске бронирования: " + e.getMessage());
        }
    }
}
