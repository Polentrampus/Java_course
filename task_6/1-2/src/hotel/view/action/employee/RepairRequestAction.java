package hotel.view.action.employee;

import hotel.controller.AdminController;
import hotel.view.action.BaseAction;

import java.util.Scanner;

public class RepairRequestAction extends BaseAction {
    private final AdminController adminController;

    public RepairRequestAction(AdminController adminController, Scanner scanner) {
        super(scanner);
        this.adminController = adminController;
    }

    @Override
    public void execute() {
        try {
            System.out.println("\n=== ЗАПРОС НА РЕМОНТ ===");
            int roomId = readInt("Введите ID комнаты для ремонта: ");
            adminController.repairRequest(roomId);
            System.out.println("Запрос на ремонт отправлен!");
        } catch (Exception e) {
            System.out.println("Ошибка при отправке запроса на ремонт: " + e.getMessage());
        }
    }
}