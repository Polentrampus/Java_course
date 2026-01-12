package hotel.view.action.employee;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class CleaningRequestAction extends BaseAction {
    private final AdminController adminController;

    public CleaningRequestAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЗАПРОС НА УБОРКУ ===");
            int roomId = readInt("Введите ID комнаты для уборки: ");
            adminController.cleaningRequest(roomId);
            System.out.println("Запрос на уборку отправлен!");
        } catch (Exception e) {
            System.out.println("Ошибка при отправке запроса на уборку: " + e.getMessage());
        }
    }
}